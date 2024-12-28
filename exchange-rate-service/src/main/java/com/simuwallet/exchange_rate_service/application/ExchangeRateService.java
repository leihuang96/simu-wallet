package com.simuwallet.exchange_rate_service.application;

import com.simuwallet.exchange_rate_service.client.ExchangeRateClient;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ExchangeRateService {
    private final ExchangeRateClient exchangeRateClient;
    private final StringRedisTemplate redisTemplate;

    public ExchangeRateService(ExchangeRateClient exchangeRateClient, StringRedisTemplate redisTemplate) {
        this.exchangeRateClient = exchangeRateClient;
        this.redisTemplate = redisTemplate;
    }

    private String getCacheKey(String baseCurrency, String targetCurrency) {
        return "exchange_rate:" + baseCurrency + ":" + targetCurrency;
    }

    private void saveToCache(String key, Double rate) {
        redisTemplate.opsForValue().set(key, String.valueOf(rate), 60, TimeUnit.MINUTES);
    }

    private Double getFromCache(String key) {
        String cachedValue = redisTemplate.opsForValue().get(key);
        return cachedValue != null ? Double.valueOf(cachedValue) : null;
    }

    public Map<String, Double> getAllExchangeRates(String baseCurrency) {
        Map<String, Map<String, Double>> response = exchangeRateClient.getExchangeRates(baseCurrency);

        // 从返回结果中提取 rates 部分
        Map<String, Double> rates = response.get("rates");
        if (rates == null || rates.isEmpty()) {
            throw new IllegalStateException("Rates data is missing or empty in the response");
        }

        // 缓存更新和返回结果
        rates.forEach((currency, rate) -> {
            String cacheKey = getCacheKey(baseCurrency, currency);
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(rate), 60, TimeUnit.MINUTES);
        });

        return rates;
    }

    public Double getExchangeRate(String baseCurrency, String targetCurrency) {
        try {
            String cacheKey = getCacheKey(baseCurrency, targetCurrency);
            Double cachedRate = getFromCache(cacheKey);

            // 优先从缓存获取
            if (cachedRate != null) return cachedRate;

            // 调用 Feign Client 获取汇率数据
            Map<String, Map<String, Double>> response = exchangeRateClient.getExchangeRates(baseCurrency);

            // 提取 "rates" 部分
            Map<String, Double> rates = response.get("rates");
            if (rates == null || rates.isEmpty()) {
                throw new IllegalStateException("Rates data is missing or empty in the response");
            }

            // 获取目标币种汇率
            Double rate = rates.get(targetCurrency);
            if (rate == null) {
                throw new IllegalArgumentException("Target currency not found: " + targetCurrency);
            }

            // 缓存结果
            saveToCache(cacheKey, rate);
            return rate;

        } catch (Exception e) {
            System.err.println("Error fetching exchange rate: " + e.getMessage());
            throw new RuntimeException("Failed to fetch exchange rate", e);
        }
    }


    public Map<String, Double> getExchangeRatesForUser(String baseCurrency, List<String> userCurrencies) {
        Map<String, Double> userRates = new HashMap<>();
        List<String> keys = new ArrayList<>();
        userCurrencies.forEach(currency -> keys.add(getCacheKey(baseCurrency, currency)));

        // 批量查询缓存
        List<String> cachedRates = redisTemplate.opsForValue().multiGet(keys);
        List<String> missingCurrencies = new ArrayList<>();

        for (int i = 0; i < userCurrencies.size(); i++) {
            String currency = userCurrencies.get(i);
            String cachedRate = cachedRates.get(i);

            if (cachedRate != null) {
                userRates.put(currency, Double.valueOf(cachedRate));
            } else {
                missingCurrencies.add(currency);
            }
        }

        // 如果有未命中缓存的币种，从 API 获取
        if (!missingCurrencies.isEmpty()) {
            Map<String, Map<String, Double>> response = exchangeRateClient.getExchangeRates(baseCurrency);
            Map<String, Double> rates = response.get("rates");

            if (rates == null || rates.isEmpty()) {
                throw new IllegalStateException("Rates data is missing or empty in the response");
            }

            for (String currency : missingCurrencies) {
                if (rates.containsKey(currency)) {
                    Double rate = rates.get(currency);
                    userRates.put(currency, rate);
                    saveToCache(getCacheKey(baseCurrency, currency), rate);
                }
            }
        }

        return userRates;
    }


    @Scheduled(fixedRate = 3600000)
    public void updateAllExchangeRates() {
        Map<String, Map<String, Double>> response = exchangeRateClient.getExchangeRates("USD");
        Map<String, Double> rates = response.get("rates");

        if (rates == null || rates.isEmpty()) {
            throw new IllegalStateException("Rates data is missing or empty in the response");
        }

        // 使用 Pipeline 更新缓存
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            rates.forEach((currency, rate) -> {
                String cacheKey = getCacheKey("USD", currency);
                connection.setEx(
                        redisTemplate.getStringSerializer().serialize(cacheKey), // Key
                        3600,                                                   // 过期时间（秒）
                        redisTemplate.getStringSerializer().serialize(String.valueOf(rate)) // Value
                );
            });
            return null;
        });
    }
}
