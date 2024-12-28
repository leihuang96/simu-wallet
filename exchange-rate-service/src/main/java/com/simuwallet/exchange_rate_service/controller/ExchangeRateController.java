package com.simuwallet.exchange_rate_service.controller;

import com.simuwallet.exchange_rate_service.application.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/exchange-rate")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    // 查询所有汇率
    @GetMapping("/{baseCurrency}")
    public Map<String, Double> getAllExchangeRates(@PathVariable String baseCurrency) {
        return exchangeRateService.getAllExchangeRates(baseCurrency);
    }

    // 查询指定基准货币和目标货币
    @GetMapping("/{baseCurrency}/{targetCurrency}")
    public Double getExchangeRate(@PathVariable String baseCurrency, @PathVariable String targetCurrency) {
        return exchangeRateService.getExchangeRate(baseCurrency, targetCurrency);
    }
}