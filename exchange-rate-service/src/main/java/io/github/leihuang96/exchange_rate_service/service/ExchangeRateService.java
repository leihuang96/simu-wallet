package io.github.leihuang96.exchange_rate_service.service;

import io.github.leihuang96.exchange_rate_service.cache.RedisCacheManager;
import io.github.leihuang96.exchange_rate_service.client.ExchangeRateApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

// 核心业务逻辑
@Service
public class ExchangeRateService {

    @Autowired
    private ExchangeRateApiClient exchangeRateApiClient;

    @Autowired
    private RedisCacheManager redisCacheManager;

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);


    /**
     * 获取并缓存汇率
     */
    public void updateExchangeRates(String baseCurrency) {
        logger.info("Updating exchange rates for base currency: {}", baseCurrency);

        try {
            exchangeRateApiClient.fetchAndCacheExchangeRates(baseCurrency);
            logger.info("Exchange rates updated for base currency: {}", baseCurrency);
        } catch (Exception e) {
            logger.error("Failed to update exchange rates for base currency: {}", baseCurrency, e);
            throw e;
        }
    }

    /**
     * 从 Redis 获取汇率
     * @param baseCurrency 源货币
     * @param targetCurrency 目标货币
     * @return 汇率
     */
    public BigDecimal getExchangeRate(String baseCurrency, String targetCurrency) {
        logger.debug("Fetching exchange rate from {} to {}", baseCurrency, targetCurrency);
        String cachedRate = redisCacheManager.getRateFromCache(baseCurrency, targetCurrency);
        if (cachedRate != null) {
            logger.info("Cache hit for exchange rate from {} to {}: {}", baseCurrency, targetCurrency, cachedRate);
            return new BigDecimal(cachedRate);
        }

        // 如果缓存中不存在，自动更新并重新尝试获取
        logger.warn("Cache miss for exchange rate from {} to {}, attempting to update rates", baseCurrency, targetCurrency);
        updateExchangeRates(baseCurrency);

        cachedRate = redisCacheManager.getRateFromCache(baseCurrency, targetCurrency);
        if (cachedRate == null) {
            logger.error("Exchange rate not available after updating for {} to {}", baseCurrency, targetCurrency);
            throw new RuntimeException("Exchange rate not available for " + baseCurrency + " to " + targetCurrency);
        }
        logger.info("Successfully retrieved exchange rate from {} to {}: {}", baseCurrency, targetCurrency, cachedRate);
        return new BigDecimal(cachedRate);
    }

    /**
     * 转换金额逻辑
     * @param baseCurrency 源货币
     * @param targetCurrency 目标货币
     * @param amount 转换金额
     * @return 转换后的金额
     */
    public BigDecimal convert(String baseCurrency, String targetCurrency, BigDecimal amount) {
        // 模拟调用汇率服务，返回汇率
        BigDecimal exchangeRate = getExchangeRate(baseCurrency, targetCurrency);
        // 计算转换后的金额
        BigDecimal convertedAmount =amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        return convertedAmount;
    }
}
