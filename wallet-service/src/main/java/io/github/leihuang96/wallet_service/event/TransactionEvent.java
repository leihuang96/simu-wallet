package io.github.leihuang96.wallet_service.event;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionEvent {
    private Long userId; // 用户 ID
    private String type; // 交易类型
    private BigDecimal sourceAmount; // 支出金额
    private String sourceCurrency; // 支出币种
    private BigDecimal targetAmount; // 转换后金额（可选）
    private String targetCurrency; // 转换后币种（可选）
    private BigDecimal fee; // 手续费
    private String feeCurrency; // 手续费币种
    private String status; // 状态
    private String stockSymbol; // 股票代码（仅股票交易）
    private BigDecimal quantity; // 股票数量（仅股票交易）
    private BigDecimal exchangeRate; // 汇率（仅货币转换）
    private LocalDateTime initiatedAt; // 发起时间
}