package io.github.leihuang96.wallet_service.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import io.github.leihuang96.common_module.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionEventPublisher {
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TransactionEventPublisher.class);

    public TransactionEventPublisher(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionEvent(TransactionEvent event) {
        kafkaTemplate.send("transaction-topic", event);
        logger.info("Sent transaction event: {}", event);
    }
}
