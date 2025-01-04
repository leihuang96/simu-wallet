package io.github.leihuang96.exchange_rate_service.event;

import io.github.leihuang96.exchange_rate_service.service.ExchangeRateService;
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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConversionRequestConsumer.class);
    private static final String CONVERSION_RESPONSE_TOPIC = "conversion-response";

    /**
     * 监听 `conversion-request` 主题并处理金额转换逻辑
     */
    @KafkaListener(topics = "conversion-request", groupId = "exchange-rate-service-group")
    public void handleConversionRequest(String message) {

            // 消息格式：userId:baseCurrency:targetCurrency:amount
            String[] parts = message.split(":");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Invalid message format. Expected 'baseCurrency:targetCurrency:amount'");
            }

            String userId = parts[0];
            String baseCurrency = parts[1];
            String targetCurrency = parts[2];
            BigDecimal amount = new BigDecimal(parts[3]);

            // 调用汇率转换逻辑
            BigDecimal convertedAmount = exchangeRateService.convert(baseCurrency, targetCurrency, amount);
            logger.info("Converted amount: {}", convertedAmount);

            // 构造响应消息
            String responseMessage = String.format("%s:%s:%s:%s:%s",userId, baseCurrency, targetCurrency, amount, convertedAmount);
            logger.info("Response message: {}", responseMessage);

            // 发送转换结果到 `conversion-response` 主题
            try {
                kafkaTemplate.send(CONVERSION_RESPONSE_TOPIC, responseMessage);
                logger.info("Sent conversion response to Kafka: {}", responseMessage);
            }
            catch (Exception e) {
                logger.error("Error sending conversion response: {}", e.getMessage());
                throw new RuntimeException(e);
            }
    }
}
