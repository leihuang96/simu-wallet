package com.simuwallet.wallet_service.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExchangeRateResponseListener {

    // 缓存响应数据
    private final ConcurrentHashMap<String, Double> exchangeRateCache = new ConcurrentHashMap<>();

    // 监听 "exchange-rate-responses" 主题的消息
    @KafkaListener(topics = "exchange-rate-responses", groupId = "wallet-service-group")
    public void handleExchangeRateResponse(String message) {
        try {
            // 消息格式：baseCurrency:targetCurrency:rate
            String[] parts = message.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid message format. Expected 'baseCurrency:targetCurrency:rate'");
            }

            String baseCurrency = parts[0];
            String targetCurrency = parts[1];
            Double rate = Double.valueOf(parts[2]);

            // 缓存汇率数据
            String cacheKey = baseCurrency + ":" + targetCurrency;
            exchangeRateCache.put(cacheKey, rate);

            System.out.printf("Cached exchange rate: %s -> %s = %f%n", baseCurrency, targetCurrency, rate);

        } catch (Exception e) {
            System.err.println("Error processing exchange rate response: " + e.getMessage());
        }
    }

    // 提供一个方法供其他服务获取缓存的汇率
    public Double getCachedExchangeRate(String baseCurrency, String targetCurrency) {
        return exchangeRateCache.get(baseCurrency + ":" + targetCurrency);
    }
}