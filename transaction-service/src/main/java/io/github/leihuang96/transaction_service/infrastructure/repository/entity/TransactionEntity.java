package io.github.leihuang96.transaction_service.infrastructure.repository.entity;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transaction")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", unique = true)
    private String transactionId; // 自增数值型字段

    @Column(name = "user_id")
    private String userId;

    private String type;

    @Column(name = "source_amount", precision = 20, scale = 2)
    private BigDecimal sourceAmount;

    @Column(name = "source_currency")
    private String sourceCurrency;

    @Column(name = "target_amount", precision = 20, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "target_currency")
    private String targetCurrency;

    @Column(name = "stock_symbol")
    private String stockSymbol;

    @Column(precision = 20, scale = 8)
    private BigDecimal quantity;

    @Column(precision = 20, scale = 2)
    private BigDecimal fee;

    @Column(name = "fee_currency")
    private String feeCurrency;

    @Column(name = "exchange_rate", precision = 20, scale = 8)
    private BigDecimal exchangeRate;

    @Column(name = "initiated_at")
    private LocalDateTime initiatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(nullable = false)
    private String status; // "CREATED","PROCESSING", "COMPLETED", "FAILED"

    @Column
    private String description;

    // 格式化 transaction_id 为固定长度
    public String getFormattedTransactionId() {
        // 将字符串解析为整数，然后格式化为固定长度
        int id = Integer.parseInt(transactionId);
        return String.format("%06d", id);
    }

}
