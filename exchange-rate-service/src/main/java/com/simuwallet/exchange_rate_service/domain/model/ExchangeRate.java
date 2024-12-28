package com.simuwallet.exchange_rate_service.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ExchangeRate {
    private String baseCurrency;
    private Map<String, Double> rates;
    private LocalDateTime timestamp;
}