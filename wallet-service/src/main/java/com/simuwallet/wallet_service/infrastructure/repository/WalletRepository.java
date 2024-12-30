package com.simuwallet.wallet_service.infrastructure.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.simuwallet.wallet_service.infrastructure.repository.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<WalletEntity, String> {
    // 获取下一个钱包 ID
    @Query(value = "SELECT nextval('user_wallet_id_seq')", nativeQuery = true)
    Long getNextWalletId();

    // 根据 wallet_id 查询钱包
    Optional<WalletEntity> findByWalletId(String walletId);

    // 根据 user_id 和 currency_code 查询特定用户的钱包
    Optional<WalletEntity> findByUserIdAndCurrencyCode(String userId, String currencyCode);

    // 查询某用户的所有钱包
    List<WalletEntity> findByUserId(String userId);

    // 根据 currency_code 查询wallet_id
    Optional<WalletEntity> findByCurrencyCode(String currencyCode);
}
