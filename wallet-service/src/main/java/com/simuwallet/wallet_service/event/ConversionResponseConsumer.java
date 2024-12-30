package com.simuwallet.wallet_service.event;
import com.simuwallet.wallet_service.application.WalletApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConversionResponseConsumer {
    @Autowired
    private WalletApplicationService walletApplicationService;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConversionResponseConsumer.class);

    /**
     * 监听 `conversion-response` 主题并更新钱包余额
     */
    @KafkaListener(topics = "conversion-response", groupId = "wallet-service-group")
    public void handleConversionResponse(String message) {
        logger.info("Received message from 'conversion-response': {}", message);
        try {
            // 消息格式：fromWalletId:toWalletId:amount:convertedAmount
            String[] parts = message.split(":");
            if (parts.length != 4) {
                logger.info("Received message from 'conversion-response': {}", message);
            }

            String baseCurrency = parts[0];
            String targetCurrency = parts[1];
            BigDecimal amount = new BigDecimal(parts[2]);
            BigDecimal convertedAmount = new BigDecimal(parts[3]);

            // 更新钱包余额
            walletApplicationService.updateWalletBalances(baseCurrency, targetCurrency, amount, convertedAmount);
            logger.info("Updated wallets: fromWalletId={}, toWalletId={}", baseCurrency, targetCurrency);
        } catch (Exception e) {
            logger.error("Error processing conversion response: {}", e.getMessage());
        }
    }
}
