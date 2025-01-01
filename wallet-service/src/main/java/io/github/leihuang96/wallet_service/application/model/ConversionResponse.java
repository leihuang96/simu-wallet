package io.github.leihuang96.wallet_service.application.model;

import org.springframework.stereotype.Component;

public class ConversionResponse {
    private String baseCurrency;
    private String targetCurrency;

    public ConversionResponse(String baseCurrency, String targetCurrency) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }
}