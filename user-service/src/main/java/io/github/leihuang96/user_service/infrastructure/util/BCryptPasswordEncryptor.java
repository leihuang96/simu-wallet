package io.github.leihuang96.user_service.infrastructure.util;

import io.github.leihuang96.user_service.domain.service.PasswordEncryptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


// 提供 PasswordEncryptor 的具体实现，使用 BCrypt 进行加密和匹配。
@Component
public class BCryptPasswordEncryptor implements PasswordEncryptor {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encrypt(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Raw password cannot be null or blank.");
        }
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Raw password cannot be null or blank.");
        }
        if (encryptedPassword == null || encryptedPassword.isBlank()) {
            throw new IllegalArgumentException("Encrypted password cannot be null or blank.");
        }
        return encoder.matches(rawPassword, encryptedPassword);
    }
}