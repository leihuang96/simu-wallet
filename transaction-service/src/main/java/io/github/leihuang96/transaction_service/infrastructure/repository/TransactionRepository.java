package io.github.leihuang96.transaction_service.infrastructure.repository;

import io.github.leihuang96.transaction_service.infrastructure.repository.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    @Query(
            "SELECT t FROM TransactionEntity t WHERE " +
                    "(:type IS NULL OR t.type = :type) AND " +
                    "(:currency IS NULL OR t.sourceCurrency = :currency OR t.targetCurrency = :currency) AND " +
                    "(:start IS NULL OR t.initiatedAt >= :start) AND " +
                    "(:end IS NULL OR t.initiatedAt <= :end)"
    )
    Page<TransactionEntity> findByFilters(
            @Param("type") String type,
            @Param("currency") String currency,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );
}