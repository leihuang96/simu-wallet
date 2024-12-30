package com.simuwallet.wallet_service.controller;

import com.simuwallet.wallet_service.application.WalletApplicationService;
import com.simuwallet.wallet_service.application.model.ConversionResponse;
import com.simuwallet.wallet_service.domain.model.Wallet;
import com.simuwallet.wallet_service.event.ConversionRequestProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallets")
public class WalletController {
    private final WalletApplicationService walletApplicationService;

    @Autowired
    private ConversionRequestProducer producer;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WalletController.class);

    @Autowired
    public WalletController(WalletApplicationService walletApplicationService) {
        this.walletApplicationService = walletApplicationService;
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 存款
    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable String walletId,
            @RequestParam BigDecimal amount) {
        walletApplicationService.deposit(walletId, amount);
        return ResponseEntity.ok("Deposit successful");
    }

    // 取款
    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<String> withdraw(
            @PathVariable String walletId,
            @RequestParam BigDecimal amount) {
        walletApplicationService.withdraw(walletId, amount);
        return ResponseEntity.ok("Withdrawal successful");
    }

    // 跨币种转换
    @PostMapping("/{fromWalletId}/convert")
    public ResponseEntity<String> convert(
            @PathVariable String fromWalletId,
            @RequestParam String toWalletId,
            @RequestParam BigDecimal amount) {
        ConversionResponse response = walletApplicationService.convert(fromWalletId, toWalletId, amount);

        try {
            producer.sendConversionRequest(response.getBaseCurrency(), response.getTargetCurrency(), amount);
            logger.info("Conversion request sent successfully");
        }
        catch (Exception e) {
            logger.error("Error sending conversion request: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Currency conversion successful");
    }
}
