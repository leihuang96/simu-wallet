package com.simuwallet.exchange_rate_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

// 调用外部 API，获取所有汇率数据。
@FeignClient(name = "exchangeRateClient", url = "${exchange-rate.api-url}")
public interface ExchangeRateClient {
    // 获取某基准货币对所有目标币种的汇率
    @GetMapping("/exchange-rate/{baseCurrency}")
    Map<String, Map<String, Double>> getExchangeRates(@PathVariable("baseCurrency") String baseCurrency);

    // 获取单一基准货币对某一目标币种的汇率
    @GetMapping("/exchange-rate/{baseCurrency}/{targetCurrency}")
    Double getExchangeRate(@PathVariable("baseCurrency") String baseCurrency,
                           @PathVariable("targetCurrency") String targetCurrency);

}