package io.github.leihuang96.exchange_rate_service.event;

import io.github.leihuang96.common_module.ConversionResponseEvent;
import io.github.leihuang96.common_module.TransactionEvent;
import io.github.leihuang96.exchange_rate_service.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConversionRequestConsumer {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConversionRequestConsumer.class);
    private static final String CONVERSION_RESPONSE_TOPIC = "conversion-response";
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private KafkaTemplate<String, ConversionResponseEvent> kafkaTemplate;

    /**
     * 监听 `conversion-request` 主题并处理金额转换逻辑
     */
    @KafkaListener(topics = "conversion-request", groupId = "exchange-rate-service-group", containerFactory = "conversionRequestContainerFactory")
    public void handleConversionRequest(TransactionEvent transactionEvent) {
        if (transactionEvent == null) {
            logger.error("Received null TransactionEvent. Check message format and serialization.");
            return;
        }
        try {
            // 调用汇率转换逻辑
            BigDecimal convertedAmount = exchangeRateService.convert(
                    transactionEvent.getSourceCurrency(),
                    transactionEvent.getTargetCurrency(),
                    transactionEvent.getSourceAmount()
            );

            // 构造响应消息
            ConversionResponseEvent response = new ConversionResponseEvent();
            response.setUserId(transactionEvent.getUserId());
            response.setSourceCurrency(transactionEvent.getSourceCurrency());
            response.setTargetCurrency(transactionEvent.getTargetCurrency());
            response.setSourceAmount(transactionEvent.getSourceAmount());
            response.setConvertedAmount(convertedAmount);

            // 发送转换结果到 `conversion-response` 主题
            kafkaTemplate.send(CONVERSION_RESPONSE_TOPIC, response);
            logger.info("Conversion response sent: {}", response);

        }
        catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage());
        }
    }
}