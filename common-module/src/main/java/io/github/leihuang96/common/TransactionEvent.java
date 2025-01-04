package io.github.leihuang96.common;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionEvent {
    private String transactionId;
    private String userId;
    private String type;
    private BigDecimal sourceAmount;
    private String sourceCurrency;
    private BigDecimal targetAmount;
    private String targetCurrency;
    private String stockSymbol;
    private BigDecimal quantity;
    private BigDecimal fee;
    private String feeCurrency;
    private BigDecimal exchangeRate;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private String status;
    private String description;
}