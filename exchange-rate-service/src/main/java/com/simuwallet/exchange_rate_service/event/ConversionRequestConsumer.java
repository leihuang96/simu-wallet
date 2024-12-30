package com.simuwallet.exchange_rate_service.event;

import com.simuwallet.exchange_rate_service.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConversionRequestConsumer {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String CONVERSION_RESPONSE_TOPIC = "conversion-response";

    /**
     * 监听 `conversion-request` 主题并处理金额转换逻辑
     */
    @KafkaListener(topics = "conversion-request", groupId = "exchange-rate-service-group")
    public void handleConversionRequest(String message) {
        try {
            // 消息格式：baseCurrency:targetCurrency:amount
            String[] parts = message.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid message format. Expected 'baseCurrency:targetCurrency:amount'");
            }

            String baseCurrency = parts[0];
            String targetCurrency = parts[1];
            BigDecimal amount = new BigDecimal(parts[2]);

            // 调用汇率转换逻辑
            BigDecimal convertedAmount = exchangeRateService.convert(baseCurrency, targetCurrency, amount);

            // 构造响应消息
            String responseMessage = String.format("%s:%s:%s:%s", baseCurrency, targetCurrency, amount, convertedAmount);

            // 发送转换结果到 `conversion-response` 主题
            kafkaTemplate.send(CONVERSION_RESPONSE_TOPIC, responseMessage);
            System.out.println("Sent conversion response to Kafka: " + responseMessage);
        } catch (Exception e) {
            System.err.println("Error processing conversion request: " + e.getMessage());
        }
    }
}