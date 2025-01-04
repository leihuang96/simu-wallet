package io.github.leihuang96.common_module;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionEvent {
    @JsonInclude
    private String transactionId;
    private String userId;
    @JsonInclude
    private String type;
    private BigDecimal sourceAmount;
    private String sourceCurrency;
    @JsonInclude
    private BigDecimal targetAmount;
    @JsonInclude
    private String targetCurrency;
    @JsonInclude
    private String stockSymbol;
    @JsonInclude
    private BigDecimal quantity;
    @JsonInclude
    private BigDecimal fee;
    @JsonInclude
    private String feeCurrency;
    @JsonInclude
    private BigDecimal exchangeRate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime initiatedAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime completedAt;
    @JsonInclude
    private String status;
    @JsonInclude
    private String description;

    public TransactionEvent() {}

}
