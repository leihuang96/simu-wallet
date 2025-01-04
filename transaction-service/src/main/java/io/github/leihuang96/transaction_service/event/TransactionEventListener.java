package io.github.leihuang96.transaction_service.event;

import io.github.leihuang96.common.TransactionEvent;
import io.github.leihuang96.transaction_service.application.TransactionApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionEventListener.class);
    private final TransactionApplicationService transactionApplicationService;

    public TransactionEventListener(TransactionApplicationService transactionApplicationService) {
        this.transactionApplicationService = transactionApplicationService;
    }

    @KafkaListener(topics = "transaction-topic", groupId = "transaction-service-group")
    public void handleTransactionEvent(TransactionEvent event) {
        logger.info("Received transaction event: {}", event);
        try {
            // 调用 TransactionService 处理事件
            transactionApplicationService.processTransactionEvent(event);
            logger.info("Processed transaction event successfully for userId: {}", event.getUserId());
        }
        catch (Exception e) {
            logger.error("Error processing transaction event: {}", e.getMessage(), e);
        }
    }
}