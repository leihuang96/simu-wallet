package com.simuwallet.wallet_service.application.impl;

import com.simuwallet.wallet_service.application.WalletApplicationService;
import com.simuwallet.wallet_service.domain.model.Wallet;
import com.simuwallet.wallet_service.domain.service.CurrencyService;
import com.simuwallet.wallet_service.domain.service.WalletDomainService;
import com.simuwallet.wallet_service.event.ConversionRequestProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletApplicationServiceImpl implements WalletApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(WalletApplicationServiceImpl.class);
    private final WalletDomainService walletDomainService;
    private final CurrencyService currencyService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ConversionRequestProducer conversionRequestProducer;

    @Autowired
    public WalletApplicationServiceImpl(WalletDomainService walletDomainService, CurrencyService currencyService,
                                        KafkaTemplate<String, String> kafkaTemplate) {
        this.walletDomainService = walletDomainService;
        this.currencyService = currencyService;
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

    @Override
    public void convert(String fromWalletId, String toWalletId, BigDecimal amount) {
        // 获取钱包信息
        Wallet fromWallet = walletDomainService.getWallet(fromWalletId);
        Wallet toWallet = walletDomainService.getWallet(toWalletId);

        // 提取货币信息
        String baseCurrency = fromWallet.getCurrencyCode();
        String targetCurrency = toWallet.getCurrencyCode();


        // Step 3: 构造消息字符串，仅包含 baseCurrency, targetCurrency, amount
        String message = String.format("%s:%s:%s", baseCurrency, targetCurrency, amount);

        // Step 4: 发送消息到 Kafka
        kafkaTemplate.send("conversion-request", message);

        logger.info("Conversion request sent: {}", message);
    }

    public void updateWalletBalances(String baseCurrency, String targetCurrency, BigDecimal amount,
                                     BigDecimal convertedAmount) {
        String fromWalletId = walletDomainService.getWalletIdByCurrency(baseCurrency);
        String toWalletId = walletDomainService.getWalletIdByCurrency(targetCurrency);

        withdraw(fromWalletId, amount);
        deposit(toWalletId, convertedAmount);

        System.out.println("Updated wallets: fromWalletId=" + fromWalletId + ", toWalletId=" + toWalletId);
    }
}
