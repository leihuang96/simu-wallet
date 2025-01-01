package io.github.leihuang96.transaction_service.controller;

import io.github.leihuang96.transaction_service.application.TransactionApplicationService;
import io.github.leihuang96.transaction_service.infrastructure.repository.entity.TransactionEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionApplicationService transactionApplicationService;

    public TransactionController(TransactionApplicationService transactionApplicationService) {
        this.transactionApplicationService = transactionApplicationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getTransaction(@PathVariable Long id) {
        TransactionEntity transaction = transactionApplicationService.getTransactionById(id);
        Map<String, String> response = new HashMap<>();
        response.put("transactionId", transaction.getFormattedTransactionId());
        response.put("type", transaction.getType());
        return ResponseEntity.ok(response);
    }
}
