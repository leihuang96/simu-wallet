package io.github.leihuang96.wallet_service.infrastructure.mapper;

import io.github.leihuang96.wallet_service.domain.model.Wallet;
import io.github.leihuang96.wallet_service.infrastructure.repository.entity.WalletEntity;

import java.util.List;

//在实体（Entity）和领域对象（Domain）之间转换。
//仅处理数据结构的映射，不包含业务逻辑。
public interface WalletMapper {
    List<WalletEntity> toEntities(Wallet wallet);
    Wallet toDomain(WalletEntity walletEntity);
}
