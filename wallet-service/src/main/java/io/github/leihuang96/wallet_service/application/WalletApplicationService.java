package io.github.leihuang96.wallet_service.application;

import io.github.leihuang96.wallet_service.application.model.ConversionResponse;
import io.github.leihuang96.wallet_service.domain.model.Wallet;

import java.math.BigDecimal;

// Application Service（应用服务） 负责协调用例和业务流程，是领域层和外部世界（如控制器层、用户接口层）之间的桥梁。
// Application Service（应用服务） 它通常直接调用领域服务或领域对象
public interface WalletApplicationService {
    void createWallet(String userId, String currencyCode);

    Wallet getWallet(String walletId);

    void deposit(String walletId, BigDecimal amount);

    void withdraw(String walletId, BigDecimal amount);

    ConversionResponse convert(String fromWalletId, String toWalletId, BigDecimal amount);

    void deleteWallet(String walletId);

    void updateWalletBalances(String baseCurrency, String targetCurrency, BigDecimal amount, BigDecimal convertedAmount);

    void createAndSendTransactionEvent(String baseCurrency, String targetCurrency,String userId,BigDecimal amount,BigDecimal convertedAmount);
}
