package com.simuwallet.exchange_rate_service.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ExchangeRateProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendExchangeRateResponse(String baseCurrency, String targetCurrency, Double rate) {
        String response = String.format("%s:%s:%f", baseCurrency, targetCurrency, rate);
        kafkaTemplate.send("exchange-rate-responses", response);
    }
}
