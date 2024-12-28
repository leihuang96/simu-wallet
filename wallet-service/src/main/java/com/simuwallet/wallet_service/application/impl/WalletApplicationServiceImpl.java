package com.simuwallet.wallet_service.application.impl;

import com.simuwallet.wallet_service.application.WalletApplicationService;
import com.simuwallet.wallet_service.domain.model.Wallet;
import com.simuwallet.wallet_service.domain.service.CurrencyService;
import com.simuwallet.wallet_service.domain.model.valueobject.Currency;
import com.simuwallet.wallet_service.domain.service.WalletDomainService;
import com.simuwallet.wallet_service.event.ExchangeRateResponseListener;
import com.simuwallet.wallet_service.infrastructure.mapper.WalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WalletApplicationServiceImpl implements WalletApplicationService {
    private final ExchangeRateResponseListener exchangeRateResponseListener;
    private final WalletDomainService walletDomainService;
    private final CurrencyService currencyService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public WalletApplicationServiceImpl(WalletDomainService walletDomainService, CurrencyService currencyService,ExchangeRateResponseListener exchangeRateResponseListener,KafkaTemplate<String, String> kafkaTemplate) {
        this.walletDomainService = walletDomainService;
        this.currencyService = currencyService;
        this.exchangeRateResponseListener = exchangeRateResponseListener;
        this.kafkaTemplate = kafkaTemplate;

    }

    @Override
    public void createWallet(String userId, String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new IllegalArgumentException("Currency code cannot be null or empty.");
        }
        walletDomainService.createWallet(userId, currencyCode);
    }

    @Override
    public void deleteWallet(String walletId) {
        walletDomainService.deleteWallet(walletId);
    }

    @Override
    public Wallet getWallet(String walletId) {
        return walletDomainService.getWallet(walletId);
    }

    @Override
    public void deposit(String walletId, BigDecimal amount) {
        walletDomainService.deposit(walletId, amount);
    }

    @Override
    public void withdraw(String walletId, BigDecimal amount) {
        walletDomainService.withdraw(walletId, amount);
    }

    // 缓存汇率响应的异步结果
    private final ConcurrentHashMap<String, CompletableFuture<Double>> exchangeRateFutures = new ConcurrentHashMap<>();

    @Override
    public void convert(String fromWalletId, String toWalletId, BigDecimal amount) {
        Wallet fromWallet = walletDomainService.getWallet(fromWalletId);
        Wallet toWallet = walletDomainService.getWallet(toWalletId);

        String baseCurrency = fromWallet.getCurrencyCode(); // 修改为 getCurrencyCode()
        String targetCurrency = toWallet.getCurrencyCode(); // 修改为 getCurrencyCode()

        // 创建异步任务
        CompletableFuture<Double> future = new CompletableFuture<>();
        String cacheKey = baseCurrency + ":" + targetCurrency;
        exchangeRateFutures.put(cacheKey, future);
        // 发送 Kafka 消息请求汇率
        kafkaTemplate.send("exchange-rate-requests", cacheKey);
        try {
            // 等待汇率响应
            Double exchangeRate = future.get(); // 阻塞等待结果
            if (exchangeRate == null) {
                throw new IllegalStateException("Exchange rate not available");
            }

            // 完成跨币种转换
            walletDomainService.convert(fromWalletId, toWalletId, amount, BigDecimal.valueOf(exchangeRate));
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch exchange rate", e);
        } finally {
            // 清除缓存
            exchangeRateFutures.remove(cacheKey);
        }
    }

    @KafkaListener(topics = "exchange-rate-responses", groupId = "wallet-service-group")
    public void handleExchangeRateResponse(String message) {
        try {
            // 消息格式：baseCurrency:targetCurrency:rate
            String[] parts = message.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid message format. Expected 'baseCurrency:targetCurrency:rate'");
            }

            String baseCurrency = parts[0];
            String targetCurrency = parts[1];
            Double rate = Double.valueOf(parts[2]);
            String cacheKey = baseCurrency + ":" + targetCurrency;

            // 完成异步任务
            CompletableFuture<Double> future = exchangeRateFutures.get(cacheKey);
            if (future != null) {
                future.complete(rate);
            }
        } catch (Exception e) {
            System.err.println("Error handling exchange rate response: " + e.getMessage());
        }
    }
}
