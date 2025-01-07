package io.github.leihuang96.wallet_service.event;

import io.github.leihuang96.common_module.ConversionResponseEvent;
import io.github.leihuang96.wallet_service.application.WalletApplicationService;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConversionResponseConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ConversionResponseConsumer.class);
    private final Tracer tracer;
    @Autowired
    private final WalletApplicationService walletApplicationService;

    public ConversionResponseConsumer(WalletApplicationService walletApplicationService, Tracer tracer) {
        this.walletApplicationService = walletApplicationService;
        this.tracer = tracer;
    }

    /**
     * 监听 `conversion-response` 主题并更新钱包余额
     */
    @KafkaListener(topics = "conversion-response", groupId = "wallet-service-group", containerFactory =
            "conversionResponseContainerFactory")
    public void handleConversionResponse(ConversionResponseEvent responseEvent) {
        String receivedTraceId = responseEvent.getTraceId();
        tracer.nextSpan()
                .name("process-conversion-response")
                .tag("originalTraceId", receivedTraceId)
                .start();
        logger.info("Received ConversionResponseEvent from 'conversion-response': {}", responseEvent);

        try {
            // 从响应中提取数据
            String userId = responseEvent.getUserId();
            String baseCurrency = responseEvent.getSourceCurrency();
            String targetCurrency = responseEvent.getTargetCurrency();
            BigDecimal amount = responseEvent.getSourceAmount();
            BigDecimal convertedAmount = responseEvent.getConvertedAmount();

            // 更新钱包余额
            walletApplicationService.updateWalletBalances(baseCurrency, targetCurrency, amount, convertedAmount);

            // 创建并发送 TransactionEvent
            walletApplicationService.createAndSendTransactionEvent(baseCurrency, targetCurrency, userId, amount,
                    convertedAmount);
            logger.info("Updated wallets: fromWalletId={}, toWalletId={}", baseCurrency, targetCurrency);
        }
        catch (Exception e) {
            logger.error("Error processing conversion response: {}", e.getMessage());
        }
        finally {
            if (tracer.currentSpan() != null) {
                tracer.currentSpan()
                        .end();
            }
        }
    }
}