package com.simuwallet.wallet_service.domain.service;

import com.simuwallet.wallet_service.domain.exception.WalletAlreadyExistsException;
import com.simuwallet.wallet_service.domain.exception.WalletNotFoundException;
import com.simuwallet.wallet_service.domain.model.Wallet;
import com.simuwallet.wallet_service.infrastructure.mapper.WalletMapper;
import com.simuwallet.wallet_service.infrastructure.repository.WalletRepository;
import com.simuwallet.wallet_service.infrastructure.repository.entity.WalletEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletDomainService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    public WalletDomainService(
            WalletRepository walletRepository,
            WalletMapper walletMapper
    ) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
    }

    /**
     * 根据钱包 ID 获取领域模型
     */
    public Wallet getWallet(String walletId) {
        WalletEntity walletEntity = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for ID: " + walletId));
        return walletMapper.toDomain(walletEntity);
    }

    /**
     * 根据货币代码获取钱包 ID
     */
    public String getWalletIdByCurrency(String currencyCode) {
        return walletRepository.findByCurrencyCode(currencyCode)
                .map(WalletEntity::getWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for currency: " + currencyCode));
    }

    /**
     * 查询用户的所有钱包
     */
    public List<Wallet> getAllWalletsByUserId(String userId) {
        List<WalletEntity> walletEntities = walletRepository.findByUserId(userId);
        if (walletEntities.isEmpty()) {
            throw new WalletNotFoundException("No wallets found for userId: " + userId);
        }

        // 转换为领域模型
        return walletEntities.stream()
                .map(walletMapper::toDomain)
                .toList();
    }

    /**
     * 保存领域模型到数据库
     */
    @Transactional
    public void saveWallet(Wallet wallet) {
        WalletEntity existingEntity = walletRepository.findByWalletId(wallet.getWalletId())
                .orElse(null);

        if (existingEntity != null) {
            // 更新现有记录
            existingEntity.setBalance(wallet.getBalance());
            existingEntity.setModifiedAt(LocalDateTime.now());
            walletRepository.save(existingEntity);
        } else {
            // 插入新记录
            WalletEntity walletEntity = new WalletEntity(
                    wallet.getWalletId(),
                    wallet.getUserId(),
                    wallet.getCurrencyCode(),
                    wallet.getBalance()
            );
            walletRepository.save(walletEntity);
        }
    }

    /**
     * 创建新的钱包
     */
    public void createWallet(String userId, String currencyCode) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        if (walletRepository.findByUserIdAndCurrencyCode(userId, currencyCode)
                .isPresent()) {
            throw new WalletAlreadyExistsException("Wallet for this currency already exists for user: " + userId);
        }
        Wallet wallet = new Wallet(generateWalletId(), userId, currencyCode, BigDecimal.ZERO);
        saveWallet(wallet);
    }

    /**
     * 存款操作
     */
    @Transactional
    public void deposit(String walletId, BigDecimal amount) {
        Wallet wallet = getWallet(walletId); // 获取当前 Wallet
        Wallet updatedWallet = wallet.deposit(amount); // 生成新的 Wallet
        saveWallet(updatedWallet); // 保存新的 Wallet
    }

    /**
     * 取款操作
     */
    @Transactional
    public void withdraw(String walletId, BigDecimal amount) {
        Wallet wallet = getWallet(walletId); // 获取当前 Wallet
        Wallet updatedWallet = wallet.withdraw(amount); // 生成新的 Wallet
        saveWallet(updatedWallet); // 保存新的 Wallet
    }

    /**
     * 跨币种转换
     */
    public void convert(String fromWalletId, String toWalletId, BigDecimal amount, BigDecimal exchangeRate) {
        Wallet fromWallet = getWallet(fromWalletId);
        Wallet toWallet = getWallet(toWalletId);

        Wallet updatedFromWallet = fromWallet.withdraw(amount);
        BigDecimal convertedAmount = amount.multiply(exchangeRate);
        Wallet updatedToWallet = toWallet.deposit(convertedAmount);

        saveWallet(updatedFromWallet);
        saveWallet(updatedToWallet);
    }

    /**
     * 删除钱包
     */
    @Transactional
    public void deleteWallet(String walletId) {
        walletRepository.findByWalletId(walletId)
                .ifPresent(walletRepository::delete);
    }

    /**
     * 生成新的钱包 ID
     */
    public String generateWalletId() {
        Long nextId = walletRepository.getNextWalletId();
        if (nextId == null) {
            throw new IllegalStateException("Failed to generate wallet ID.");
        }
        return String.format("WA-%09d", nextId);
    }

}
