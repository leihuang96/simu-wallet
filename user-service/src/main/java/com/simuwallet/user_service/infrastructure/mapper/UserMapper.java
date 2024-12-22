package com.simuwallet.user_service.infrastructure.mapper;

import com.simuwallet.user_service.domain.model.User;
import com.simuwallet.user_service.infrastructure.repository.entity.UserEntity;

// 将领域模型 User 转换为数据库实体 UserEntity
// 负责领域模型与持久化模型之间的映射。
public interface UserMapper {
    UserEntity toEntity(User user);
    User toDomain(UserEntity entity);
}
