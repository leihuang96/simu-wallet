package io.github.leihuang96.wallet_service.application.impl;

import io.github.leihuang96.common.TransactionEvent;
import io.github.leihuang96.wallet_service.application.WalletApplicationService;
import io.github.leihuang96.wallet_service.application.model.ConversionResponse;
import io.github.leihuang96.wallet_service.domain.model.Wallet;
import io.github.leihuang96.wallet_service.domain.service.CurrencyService;
import io.github.leihuang96.wallet_service.domain.service.WalletDomainService;
import io.github.leihuang96.wallet_service.event.TransactionEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletApplicationServiceImpl implements WalletApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(WalletApplicationServiceImpl.class);
    private final WalletDomainService walletDomainService;
    private final CurrencyService currencyService;
    private final TransactionEventPublisher transactionEventPublisher;


    @Autowired
    public WalletApplicationServiceImpl(WalletDomainService walletDomainService, CurrencyService currencyService,TransactionEventPublisher transactionEventPublisher) {
        this.transactionEventPublisher = transactionEventPublisher;
        this.walletDomainService = walletDomainService;
        this.currencyService = currencyService;
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
    public ConversionResponse convert(String fromWalletId, String toWalletId, BigDecimal amount) {
        // 获取钱包信息
        Wallet fromWallet = walletDomainService.getWallet(fromWalletId);
        Wallet toWallet = walletDomainService.getWallet(toWalletId);

        // 提取货币信息
        String baseCurrency = fromWallet.getCurrencyCode();
        String targetCurrency = toWallet.getCurrencyCode();

        // 日志记录
        logger.info("Processed conversion: {} -> {} : {}", baseCurrency, targetCurrency, amount);

        // 返回转换信息
        return new ConversionResponse(baseCurrency, targetCurrency);

    }

    public void updateWalletBalances(String baseCurrency, String targetCurrency, BigDecimal amount,
                                     BigDecimal convertedAmount) {
        String fromWalletId = walletDomainService.getWalletIdByCurrency(baseCurrency);
        String toWalletId = walletDomainService.getWalletIdByCurrency(targetCurrency);

        withdraw(fromWalletId, amount);
        deposit(toWalletId, convertedAmount);

        System.out.println("Updated wallets: fromWalletId=" + fromWalletId + ", toWalletId=" + toWalletId);
    }

    @Override
    public void createAndSendTransactionEvent(String baseCurrency, String targetCurrency, String userId, BigDecimal amount, BigDecimal convertedAmount) {
        // 构建 TransactionEvent
        TransactionEvent transactionEvent = new TransactionEvent();
        transactionEvent.setUserId(userId);
        transactionEvent.setSourceAmount(amount);
        transactionEvent.setTargetAmount(convertedAmount);
        transactionEvent.setSourceCurrency(baseCurrency);
        transactionEvent.setTargetCurrency(targetCurrency);
        transactionEvent.setType("CONVERT");
        transactionEvent.setStatus("COMPLETED");
        transactionEvent.setInitiatedAt(LocalDateTime.now());

        // 发送 TransactionEvent 消息到 Kafka
        transactionEventPublisher.sendTransactionEvent(transactionEvent);
    }
}
