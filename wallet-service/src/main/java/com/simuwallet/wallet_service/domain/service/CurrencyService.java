package com.simuwallet.wallet_service.domain.service;

import com.simuwallet.wallet_service.infrastructure.repository.SupportedCurrencyRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

//从数据库加载货币列表到内存缓存。
//提供货币名称查询。

@Service
public class CurrencyService {

    private final SupportedCurrencyRepository supportedCurrencyRepository;
    private final Map<String, String> currencyCache = new HashMap<>();

    public CurrencyService(SupportedCurrencyRepository supportedCurrencyRepository) {
        this.supportedCurrencyRepository = supportedCurrencyRepository;
    }

    @PostConstruct
    public void loadCurrencies() {
        supportedCurrencyRepository.findAll()
                .forEach(currency -> currencyCache.put(currency.getCurrencyCode(), currency.getCurrencyName()));
    }

    public String getCurrencyName(String currencyCode) {
        return currencyCache.getOrDefault(currencyCode, "Unknown");
    }

    public void refreshCache() {
        currencyCache.clear();
        loadCurrencies();
    }
}