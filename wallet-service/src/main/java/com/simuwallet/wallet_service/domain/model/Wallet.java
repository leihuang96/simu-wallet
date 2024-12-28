package com.simuwallet.wallet_service.domain.model;

import com.simuwallet.wallet_service.domain.model.valueobject.Currency;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Wallet {
    private final String walletId;
    private final String userId;
    private final String currencyCode; // 只存储货币代码
    private final BigDecimal balance; // 存储余额

    public Wallet(String walletId, String userId, String currencyCode, BigDecimal balance) {
        this.walletId = walletId;
        this.userId = userId;
        this.currencyCode = currencyCode;
        this.balance = balance;
    }


    // 获取余额
    public BigDecimal getBalance(Currency currency) {
        return balance;    }

    // 增加余额：向指定币种账户存款。
    public Wallet deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        return new Wallet(walletId, userId, currencyCode, balance.add(amount));
    }


    // 扣除余额：从指定币种账户取款。
    public Wallet withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        return new Wallet(walletId, userId, currencyCode, balance.subtract(amount));
    }

    // 跨币种转换：在两个币种账户之间进行货币转换。
    public Wallet convert(BigDecimal amount, BigDecimal exchangeRate) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Conversion amount must be positive.");
        }
        if (exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive.");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance for conversion.");
        }

        return new Wallet(walletId, userId, currencyCode, balance.subtract(amount));
    }
}
