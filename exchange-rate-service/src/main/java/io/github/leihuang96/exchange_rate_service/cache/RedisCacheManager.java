package io.github.leihuang96.exchange_rate_service.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

// 管理 Redis 中的缓存数据，用于汇率信息的存储和检索。
@Component
// Redis 缓存操作
public class RedisCacheManager {


    private static final Logger logger = LoggerFactory.getLogger(RedisCacheManager.class);
    private static final String CACHE_PREFIX = "exchange-rate:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(60);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void testRedisOperation() {
        System.out.println("Redis Connection Factory: " + redisTemplate.getConnectionFactory());
    }
    /**
     * 从 Redis 获取缓存
     */
    public String getRateFromCache(String fromCurrency, String toCurrency) {
        String key = CACHE_PREFIX + fromCurrency + ":" + toCurrency;
        try {
            logger.info("Attempting to fetch rate from cache for key: {}", key);
            String rate = redisTemplate.opsForValue().get(key);
            if (rate == null) {
                logger.warn("Cache miss for key: {}", key);
            } else {
                logger.info("Cache hit for key: {}", key);
            }
            return rate;
        } catch (Exception e) {
            logger.error("Error retrieving rate from cache for key: {}", key, e);
            throw new RuntimeException("Error retrieving rate from cache", e);
        }
    }

    /**
     * 将汇率存入 Redis 缓存
     */
    public void saveRateToCache(String fromCurrency, String toCurrency, String rate) {
        String key = CACHE_PREFIX + fromCurrency + ":" + toCurrency;
        try {
            redisTemplate.opsForValue().set(key, rate, CACHE_TTL);
            logger.info("Saved rate to cache: key={}, value={}", key, rate);
        } catch (Exception e) {
            logger.error("Error saving rate to cache for key: {}", key, e);
            throw new RuntimeException("Error saving rate to cache", e);
        }
    }
}
