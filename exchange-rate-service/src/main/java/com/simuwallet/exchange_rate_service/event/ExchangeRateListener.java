package com.simuwallet.exchange_rate_service.event;

import com.simuwallet.exchange_rate_service.application.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateListener {
    @Autowired
    private ExchangeRateProducer exchangeRateProducer;
    private final ExchangeRateService exchangeRateService;

    public ExchangeRateListener(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @KafkaListener(topics = "exchange-rate-requests", groupId = "exchange-rate-group")
    public void handleExchangeRateRequest(String message) {
        try {
            String[] currencies = message.split(":");
            if (currencies.length != 2) {
                throw new IllegalArgumentException("Invalid message format. Expected 'baseCurrency:targetCurrency'");
            }

            String baseCurrency = currencies[0];
            String targetCurrency = currencies[1];
            Double rate = exchangeRateService.getExchangeRate(baseCurrency, targetCurrency);

            // 将结果发送到响应主题
            exchangeRateProducer.sendExchangeRateResponse(baseCurrency, targetCurrency, rate);

        } catch (Exception e) {
            System.err.println("Error handling exchange rate request: " + e.getMessage());
        }
    }
}