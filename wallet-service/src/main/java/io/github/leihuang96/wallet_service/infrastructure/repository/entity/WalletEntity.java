package io.github.leihuang96.wallet_service.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_wallet")
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 对应 id 字段的自增
    private Long id;

    @Column(name = "wallet_id", nullable = false, unique = true, length = 15)
    private String walletId; // 对应 wallet_id

    @Column(name = "user_id", nullable = false, length = 255)
    private String userId; // 对应 user_id，外键关联 user 表

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode; // 对应 currency_code，外键关联 supported_currency 表

    @Column(name = "balance", nullable = false)
    private BigDecimal balance; // 对应 balance

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 对应 created_at

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt; // 对应 modified_at

    public WalletEntity(String walletId, String userId, String currencyCode, BigDecimal balance) {
        this.walletId = walletId;
        this.userId = userId;
        this.currencyCode = currencyCode;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

}

