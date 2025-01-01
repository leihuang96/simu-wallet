package io.github.leihuang96.wallet_service.domain.model.valueobject;

public record Currency(String code, String name) {
    public Currency {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Currency code cannot be null or empty");
        }
        if (code.length() > 3) {
            throw new IllegalArgumentException("Currency code length must not exceed 3 characters");
        }
    }
}