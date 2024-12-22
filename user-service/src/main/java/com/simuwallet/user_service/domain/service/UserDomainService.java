package com.simuwallet.user_service.domain.service;

import com.simuwallet.user_service.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;

// 聚焦于领域逻辑,实际的数据库查询委托给 UserRepository。
// 服务层（Service Layer）负责处理 业务逻辑，但不直接与控制层（Controller）或数据访问层（Repository）交互。
@Service
public class UserDomainService {

    private final UserRepository userRepository;

    // 通过类的构造函数将所需的依赖项传递给类
    public UserDomainService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isUsernameDuplicate(String username, String userId) {
        return userRepository.existsByUsernameAndNotUserId(username, userId);
    }

    public boolean isEmailDuplicate(String email, String userId){
        return userRepository.existsByEmailAndNotUserId(email, userId);
    }
}