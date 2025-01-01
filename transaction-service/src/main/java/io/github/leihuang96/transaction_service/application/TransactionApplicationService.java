package io.github.leihuang96.transaction_service.application;

import io.github.leihuang96.transaction_service.application.export.ExcelExporter;
import io.github.leihuang96.transaction_service.application.export.PdfExporter;
import io.github.leihuang96.transaction_service.infrastructure.repository.TransactionRepository;
import io.github.leihuang96.transaction_service.infrastructure.repository.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// 应用服务层，负责协调领域逻辑、基础设施层和接口层之间的交互。
@Service
public class TransactionApplicationService {
    private final TransactionRepository transactionRepository;
    private final PdfExporter pdfExporter;
    private final ExcelExporter excelExporter;

    public TransactionApplicationService(TransactionRepository transactionRepository,
                                         PdfExporter pdfExporter,
                                         ExcelExporter excelExporter) {
        this.transactionRepository = transactionRepository;
        this.pdfExporter = pdfExporter;
        this.excelExporter = excelExporter;
    }

    public Page<TransactionEntity> getTransactions(
            String type,
            String currency,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    ) {
        return transactionRepository.findByFilters(type, currency, start, end, pageable);
    }

    public TransactionEntity getTransactionById(Long transactionId) {
        transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        return transactionRepository.findById(transactionId).get();
    }

    public void exportTransactionsToPdf(String filePath) throws Exception {
        List<TransactionEntity> transactions = transactionRepository.findAll();
        pdfExporter.exportToPdf(transactions, filePath);
    }

    public void exportTransactionsToExcel(String filePath) throws Exception {
        List<TransactionEntity> transactions = transactionRepository.findAll();
        excelExporter.exportToExcel(transactions, filePath);
    }
}
