package io.github.leihuang96.wallet_service.infrastructure.mapper.impl;

import io.github.leihuang96.wallet_service.domain.model.Wallet;
import io.github.leihuang96.wallet_service.infrastructure.mapper.WalletMapper;
import io.github.leihuang96.wallet_service.infrastructure.repository.entity.WalletEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WalletMapperImpl implements WalletMapper {
    @Override
    public List<WalletEntity> toEntities(Wallet wallet) {
        WalletEntity walletEntity = WalletEntity.builder()
                .walletId(wallet.getWalletId())
                .userId(wallet.getUserId())
                .currencyCode(wallet.getCurrencyCode())
                .balance(wallet.getBalance())
                .build();
        return List.of(walletEntity); // 返回单个 WalletEntity 的列表
    }

    @Override
    public Wallet toDomain(WalletEntity walletEntity) {
        return new Wallet(
                walletEntity.getWalletId(),
                walletEntity.getUserId(),
                walletEntity.getCurrencyCode(),
                walletEntity.getBalance()
        );
    }
}
