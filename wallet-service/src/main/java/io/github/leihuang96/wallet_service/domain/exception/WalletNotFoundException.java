package io.github.leihuang96.wallet_service.domain.exception;


public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String message) {
        super(message);
    }
}