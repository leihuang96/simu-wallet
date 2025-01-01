package io.github.leihuang96.exchange_rate_service.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

// 汇率模型
@Getter
@Setter
public class ExchangeRate {
    private String baseCurrency;
    private Map<String, Double> rates;
    private LocalDateTime timestamp;
}