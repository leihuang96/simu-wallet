package io.github.leihuang96.wallet_service.controller;

import io.github.leihuang96.common_module.TransactionEvent;
import io.github.leihuang96.wallet_service.application.WalletApplicationService;
import io.github.leihuang96.wallet_service.application.model.ConversionResponse;
import io.github.leihuang96.wallet_service.domain.model.Wallet;
import io.github.leihuang96.wallet_service.event.ConversionRequestProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger; // 日志接口
import org.slf4j.LoggerFactory; // 日志工厂，用于获取 Logger 实例
import io.micrometer.tracing.Tracer; // 用于跟踪 traceId 和 spanId
import org.springframework.web.bind.annotation.ExceptionHandler; // 用于捕获异常的方法



import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/{userId}/wallets")
public class WalletController {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WalletController.class);
    private final WalletApplicationService walletApplicationService;
    private final Tracer tracer;

    @Autowired
    private ConversionRequestProducer producer;

    @Autowired
    public WalletController(WalletApplicationService walletApplicationService, Tracer tracer) {
        this.walletApplicationService = walletApplicationService;
        this.tracer = tracer;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        String traceId = tracer.currentSpan() != null ? tracer.currentSpan().context().traceId() : "N/A";
        String spanId = tracer.currentSpan() != null ? tracer.currentSpan().context().spanId() : "N/A";

        logger.error("Exception in WalletController. TraceId: {}", traceId, e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred in WalletController. TraceId: " + traceId);
    }

    // 创建钱包
    @PostMapping
    public ResponseEntity<String> createWallet(@RequestParam String userId, @RequestParam String currencyCode) {
        walletApplicationService.createWallet(userId, currencyCode);
        return ResponseEntity.ok("Wallet created successfully");
    }

    // 获取钱包
    @GetMapping("/{walletId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String walletId) {
        Wallet wallet = walletApplicationService.getWallet(walletId);
        return ResponseEntity.ok(wallet);
    }

    // 删除钱包
    @DeleteMapping("/{walletId}")
    public ResponseEntity<String> deleteWallet(@PathVariable String walletId) {
        try {
            walletApplicationService.deleteWallet(walletId);
            return ResponseEntity.ok("Wallet deleted successfully");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // 存款
    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable String walletId,
            @RequestParam BigDecimal amount
    ) {
        walletApplicationService.deposit(walletId, amount);
        return ResponseEntity.ok("Deposit successful");
    }

    // 取款
    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<String> withdraw(
            @PathVariable String walletId,
            @RequestParam BigDecimal amount
    ) {
        walletApplicationService.withdraw(walletId, amount);
        return ResponseEntity.ok("Withdrawal successful");
    }

    // 跨币种转换
    @PostMapping("/{fromWalletId}/convert")
    public ResponseEntity<String> convert(
            @PathVariable String userId,
            @PathVariable String fromWalletId,
            @RequestParam String toWalletId,
            @RequestParam BigDecimal amount
    ) {
        var span = tracer.nextSpan().name("wallet-convert").start();
        try (var ignored = tracer.withSpan(span)) {
            ConversionResponse response = walletApplicationService.convert(fromWalletId, toWalletId, amount);

            TransactionEvent transactionEvent = new TransactionEvent();
            transactionEvent.setUserId(userId);
            transactionEvent.setSourceAmount(amount);
            transactionEvent.setSourceCurrency(response.getBaseCurrency());
            transactionEvent.setTargetCurrency(response.getTargetCurrency());
            transactionEvent.setInitiatedAt(LocalDateTime.now());
            transactionEvent.setType("CONVERT");
            transactionEvent.setStatus("PENDING");
            transactionEvent.setTraceId(span.context().traceId());
            transactionEvent.setSpanId(span.context().spanId());

            producer.sendConversionRequest(transactionEvent);
            logger.info("Conversion request sent successfully");

            return ResponseEntity.ok("Currency conversion sent successfully");
        } catch (Exception e) {
            span.tag("error", e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
