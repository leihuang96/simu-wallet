package io.github.leihuang96.common_module;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConversionResponseEvent {
    private String userId;
    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal sourceAmount;
    private BigDecimal convertedAmount;

    public ConversionResponseEvent() {}
}