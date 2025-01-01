package io.github.leihuang96.transaction_service.event;

import io.github.leihuang96.transaction_service.application.TransactionApplicationService;
import io.github.leihuang96.transaction_service.infrastructure.repository.entity.TransactionEntity;
import io.github.leihuang96.wallet_service.event.TransactionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionEventListener {
    private final TransactionApplicationService transactionApplicationService;

    public TransactionEventListener(TransactionApplicationService transactionApplicationService) {
        this.transactionApplicationService = transactionApplicationService;
    }

    @KafkaListener(topics = "transaction-topic", groupId = "transaction-service")
    public void handleTransactionEvent(TransactionEvent event) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setUserId(event.getUserId());
        transaction.setType(event.getType());
        transaction.setSourceAmount(event.getSourceAmount());
        transaction.setSourceCurrency(event.getSourceCurrency());
        transaction.setTargetAmount(event.getTargetAmount());
        transaction.setTargetCurrency(event.getTargetCurrency());
        transaction.setFee(event.getFee());
        transaction.setFeeCurrency(event.getFeeCurrency());
        transaction.setStatus(event.getStatus());
        transaction.setDescription(event.getDescription());
        transaction.setInitiatedAt(LocalDateTime.now());
        transactionApplicationService.createTransaction(transaction);
    }
}