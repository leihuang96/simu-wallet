package com.simuwallet.wallet_service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

// 用户发起金额转换请求，将请求发送到 Kafka 主题 `conversion-request`
@Service
public class ConversionRequestProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String CONVERSION_REQUEST_TOPIC = "conversion-request";

    /**
     * 发送金额转换请求到 Kafka
     */
    public void sendConversionRequest(String baseCurrency, String targetCurrency, BigDecimal amount) {
        String message = String.format("%s:%s:%s", baseCurrency, targetCurrency, amount);
        kafkaTemplate.send(CONVERSION_REQUEST_TOPIC, message);
        System.out.println("Sent conversion request to Kafka: " + message);
    }
}
