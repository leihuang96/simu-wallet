package io.github.leihuang96.user_service.infrastructure.mapper.impl;

import io.github.leihuang96.user_service.domain.model.User;
import io.github.leihuang96.user_service.domain.model.valueobject.Password;
import io.github.leihuang96.user_service.infrastructure.mapper.UserMapper;
import io.github.leihuang96.user_service.infrastructure.repository.entity.UserEntity;
import org.springframework.stereotype.Component;

// 实现UserMapper接口

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        // 使用 Builder 构建 UserEntity
        return UserEntity.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword().getEncrypted()) // 转换为加密后的密码
                .createdAt(user.getCreatedAt()) // 使用领域模型的创建时间
                .modifiedAt(user.getModifiedAt())
                .build();
    }

    @Override
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        // 使用 Builder 构建领域模型
        return User.builder()
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(Password.fromEncrypted(entity.getPassword()))
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();
    }
}
