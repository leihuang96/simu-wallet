package io.github.leihuang96.user_service.domain.model.valueobject;

import io.github.leihuang96.user_service.domain.service.PasswordEncryptor;
import lombok.EqualsAndHashCode;

// 表示领域中的密码。
// 通过 PasswordEncryptor 执行加密和验证。
@EqualsAndHashCode
public class Password {
    private final String encryptedPassword;

    private Password(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isBlank()) {
            throw new IllegalArgumentException("Encrypted password cannot be null or blank.");
        }
        this.encryptedPassword = encryptedPassword;
    }

    // 创建加密后的 Password 对象
    public static Password fromRaw(String rawPassword, PasswordEncryptor encryptor) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Raw password cannot be null or blank.");
        }
        return new Password(encryptor.encrypt(rawPassword)); // 使用加密器进行加密
    }

    // 创建已加密的 Password 对象（用于从数据库加载）
    public static Password fromEncrypted(String encryptedPassword) {
        return new Password(encryptedPassword);
    }

    // 验证原始密码是否匹配
    public boolean matches(String rawPassword, PasswordEncryptor encryptor) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Raw password cannot be null or blank.");
        }
        return encryptor.matches(rawPassword, this.encryptedPassword); // 调用加密器进行匹配验证
    }

    // 获取加密后的密码
    public String getEncrypted() {
        return encryptedPassword;
    }}
