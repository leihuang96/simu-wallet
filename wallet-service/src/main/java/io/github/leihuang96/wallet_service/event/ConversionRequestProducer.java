package io.github.leihuang96.wallet_service.event;

import io.github.leihuang96.common_module.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

// 用户发起金额转换请求，将请求发送到 Kafka 主题 `conversion-request`
@Slf4j
@Service
public class ConversionRequestProducer {

    private static final Logger logger = LoggerFactory.getLogger(ConversionRequestProducer.class);
    private static final String CONVERSION_REQUEST_TOPIC = "conversion-request";
    @Autowired
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    /**
     * 发送金额转换请求到 Kafka
     */
    public void sendConversionRequest(TransactionEvent transactionEvent) {
        validateInputs(transactionEvent);

        try {
            kafkaTemplate.send(CONVERSION_REQUEST_TOPIC, transactionEvent);
            logger.info("Sent conversion request to Kafka: {}", transactionEvent);
        }
        catch (Exception e) {
            logger.error("Error sending conversion request: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("Sent conversion request to Kafka: {}", transactionEvent);
    }


    private void validateInputs(TransactionEvent transactionEvent) {
        if (transactionEvent == null) {
            throw new IllegalArgumentException("TransactionEvent cannot be null");
        }
    }
}
