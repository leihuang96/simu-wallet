package com.simuwallet.exchange_rate_service.controller;

import com.simuwallet.exchange_rate_service.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/exchange-rate")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    /**
     * 获取指定货币对的汇率
     * @param baseCurrency 基准货币
     * @param targetCurrency 目标货币
     * @return 汇率值
     */
    @GetMapping
    public BigDecimal getExchangeRate(
            @RequestParam String baseCurrency,
            @RequestParam String targetCurrency) {
        return exchangeRateService.getExchangeRate(baseCurrency, targetCurrency);
    }

    /**
     * 强制更新指定基准货币的汇率缓存
     * @param baseCurrency 基准货币
     */
    @PostMapping("/update")
    public void updateExchangeRates(@RequestParam String baseCurrency) {
        exchangeRateService.updateExchangeRates(baseCurrency);
    }
}