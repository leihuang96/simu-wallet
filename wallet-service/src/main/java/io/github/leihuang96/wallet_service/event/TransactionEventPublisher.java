package io.github.leihuang96.wallet_service.event;

import io.github.leihuang96.transaction_service.infrastructure.repository.TransactionRepository;
import io.github.leihuang96.transaction_service.infrastructure.repository.entity.TransactionEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionEventPublisher {
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private final TransactionRepository transactionRepository;

    public TransactionEventPublisher(KafkaTemplate<String, TransactionEvent> kafkaTemplate,
                                     TransactionRepository transactionRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.transactionRepository = transactionRepository;
    }

    public TransactionEntity createTransaction(TransactionEvent event) {
        // 保存初始状态的交易
        TransactionEntity transaction = new TransactionEntity();
        transaction.setUserId(event.getUserId());
        transaction.setType(event.getType());
        transaction.setStatus("CREATED"); // 初始状态
        transaction.setSourceAmount(event.getSourceAmount());
        transaction.setSourceCurrency(event.getSourceCurrency());
        transaction.setTargetAmount(event.getTargetAmount());
        transaction.setTargetCurrency(event.getTargetCurrency());
        transaction.setInitiatedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public void updateTransactionStatus(Long transactionId, String status) {
        TransactionEntity transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        transaction.setStatus(status);
        if ("COMPLETED".equals(status)) {
            transaction.setCompletedAt(LocalDateTime.now());
        }
        transactionRepository.save(transaction);
    }

    public void sendEvent(TransactionEvent event) {
        kafkaTemplate.send("transaction-topic", event);
    }
}
