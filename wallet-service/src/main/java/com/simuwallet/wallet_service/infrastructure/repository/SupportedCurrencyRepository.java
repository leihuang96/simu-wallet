package com.simuwallet.wallet_service.infrastructure.repository;

import com.simuwallet.wallet_service.infrastructure.repository.entity.SupportedCurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportedCurrencyRepository extends JpaRepository<SupportedCurrencyEntity, String> {
    SupportedCurrencyEntity findByCurrencyCode(String currencyCode);
}
