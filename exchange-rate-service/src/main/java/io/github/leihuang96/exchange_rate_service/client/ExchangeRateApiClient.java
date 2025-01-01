package io.github.leihuang96.exchange_rate_service.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.leihuang96.exchange_rate_service.config.ApiConfig;
import io.github.leihuang96.exchange_rate_service.cache.RedisCacheManager;
import io.github.leihuang96.exchange_rate_service.service.ExchangeRateService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

// 调用第三方 API 获取汇率的客户端实现类，结合 Redis 缓存管理器，将汇率存储到缓存中

@Component
public class ExchangeRateApiClient {

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private ApiConfig apiConfig;

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);

    private final RestTemplate restTemplate;

    @Autowired
    public ExchangeRateApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 从第三方 API 获取汇率并缓存为键值对
     *
     * @param baseCurrency
     *         基准货币
     */
    public void fetchAndCacheExchangeRates(String baseCurrency) {
        try {
            //构造 URL
            String url = String.format("%s/%s", apiConfig.getBaseUrl(), baseCurrency);

            // 设置 HTTP Header
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiConfig.getKey());
            HttpEntity<String> entity = new HttpEntity<>(headers);
            logger.info("Requesting exchange rates. URL: {}", url);
            logger.info("Using API key: {}", apiConfig.getKey());
            logger.info("Constructed Headers: {}", headers);
            logger.info("Fetching exchange rates. URL: {}, Base Currency: {}", url, baseCurrency);

            // 调用第三方 API
            ResponseEntity<ExchangeRateResponse> responseEntity =
                    restTemplate.exchange(url,HttpMethod.GET,entity,
                            ExchangeRateResponse.class);

            logger.info("Raw API Response: {}", responseEntity);

            // 检查响应状态
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                logger.error("Failed to fetch exchange rates from API. Status Code: {}", responseEntity.getStatusCode());
                throw new RuntimeException("Failed to fetch exchange rates from API");
            }

            //处理响应
            ExchangeRateResponse response = responseEntity.getBody();
            if (response == null || !"success".equals(response.getResult())) {
                logger.error("Invalid API response: {}", responseEntity.getBody());
                throw new RuntimeException("Failed to fetch exchange rates from API");
            }

            // 解析响应并缓存到 Redis
            String base = response.getBaseCode();
            Map<String, BigDecimal> conversionRates = response.getConversionRates();

            logger.debug("Parsed Response Base Currency: {}", base);
            logger.debug("Parsed Response Conversion Rates: {}", conversionRates);

            for (Map.Entry<String, BigDecimal> entry : conversionRates.entrySet()) {
                String targetCurrency = entry.getKey();
                BigDecimal rate = entry.getValue();
                redisCacheManager.saveRateToCache(base, targetCurrency, rate.toString());
                logger.info("Saving exchange rate to cache: {} -> {}, Rate: {}", base, targetCurrency, rate);
            }
            logger.info("Successfully fetched exchange rates for {}", base);
        } catch (Exception e) {
            logger.error("Failed to fetch exchange rates for {}: {}", baseCurrency, e.getMessage(), e);
        }
    }

    // 封装第三方 API 的响应数据。
    public static class ExchangeRateResponse {
        @Setter
        @Getter
        private String result; // 响应结果状态
        @Setter
        @Getter
        @JsonProperty("base_code")
        private String baseCode; // 基准货币
        @Setter
        @Getter
        @JsonProperty("conversion_rates")
        private Map<String, BigDecimal> conversionRates; //汇率表（目标货币 -> 汇率值）
    }
}