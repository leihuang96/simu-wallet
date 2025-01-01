package io.github.leihuang96.wallet_service.domain.exception;

public class WalletAlreadyExistsException extends RuntimeException {
    public WalletAlreadyExistsException(String message) {
        super(message);
    }
}