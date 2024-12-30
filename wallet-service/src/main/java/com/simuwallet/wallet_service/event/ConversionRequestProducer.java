package com.simuwallet.wallet_service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;

// 用户发起金额转换请求，将请求发送到 Kafka 主题 `conversion-request`
@Slf4j
@Service
public class ConversionRequestProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ConversionRequestProducer.class);
    private static final String CONVERSION_REQUEST_TOPIC = "conversion-request";

    /**
     * 发送金额转换请求到 Kafka
     */
    public void sendConversionRequest(String baseCurrency, String targetCurrency, BigDecimal amount) {
        validateInputs(baseCurrency, targetCurrency, amount);
        String message = formatMessage(baseCurrency, targetCurrency, amount);

        try {
            kafkaTemplate.send(CONVERSION_REQUEST_TOPIC, message);
            logger.info("Sent conversion request to Kafka: {}", message);
        }
        catch (Exception e) {
            logger.error("Error sending conversion request: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("Sent conversion request to Kafka: {}", message);
    }

    /**
     * 格式化 Kafka 消息
     */
    private String formatMessage(String baseCurrency, String targetCurrency, BigDecimal amount) {
        return String.format("%s:%s:%s", baseCurrency, targetCurrency, amount);
    }

    private void validateInputs(String baseCurrency, String targetCurrency, BigDecimal amount) {
        if (baseCurrency == null || baseCurrency.isBlank()) {
            throw new IllegalArgumentException("Base currency cannot be null or empty.");
        }
        if (targetCurrency == null || targetCurrency.isBlank()) {
            throw new IllegalArgumentException("Target currency cannot be null or empty.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }
}
