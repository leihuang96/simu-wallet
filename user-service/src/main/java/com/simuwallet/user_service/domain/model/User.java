package com.simuwallet.user_service.domain.model;

import com.simuwallet.user_service.domain.model.valueobject.Password;
import com.simuwallet.user_service.domain.service.PasswordEncryptor;
import com.simuwallet.user_service.domain.service.UserDomainService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;


import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class User {
    private String userId;
    private String username;
    private String email;
    private Password password;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    // 私有构造方法，确保只能通过静态工厂方法创建实例
    private User(String userId, String username, String email, Password password, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    // 静态工厂方法，用于创建新用户
    public static User createNew(String username, String email, Password password) {
        LocalDateTime now = LocalDateTime.now();
        return new User(
                UUID.randomUUID().toString(), // userId 由系统生成
                username,
                email,
                password,
                now, // 创建时间
                now  // 初始修改时间与创建时间相同
        );
    }

    // 定义领域行为
    //当需要跨聚合的逻辑时，可以通过领域服务协调多个聚合根。

    public boolean verifyPassword(String rawPassword, PasswordEncryptor encryptor){
        return this.password.matches(rawPassword,encryptor); // 将验证逻辑封装到 Password
    }

    public static User fromEntity(String userId, String username, String email, Password password, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new User(userId, username, email, password, createdAt, modifiedAt);
    }

}
