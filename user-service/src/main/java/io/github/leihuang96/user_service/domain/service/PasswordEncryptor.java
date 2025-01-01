package io.github.leihuang96.user_service.domain.service;

// 提供加密和验证的接口定义，表示加密策略。
// 它是密码加密的核心逻辑接口，实际实现由 BCryptPasswordEncryptor 提供，职责清晰。
public interface PasswordEncryptor {

    String encrypt(String rawPassword);
    boolean matches(String rawPassword, String encryptedPassword);

}
