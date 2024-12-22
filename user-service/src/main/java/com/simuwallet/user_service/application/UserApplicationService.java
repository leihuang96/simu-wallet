package com.simuwallet.user_service.application;

import com.simuwallet.user_service.domain.model.User;
import com.simuwallet.user_service.domain.model.valueobject.Password;
import com.simuwallet.user_service.domain.service.PasswordEncryptor;
import com.simuwallet.user_service.domain.service.UserDomainService;
import com.simuwallet.user_service.infrastructure.mapper.UserMapper;
import com.simuwallet.user_service.infrastructure.repository.UserRepository;
import com.simuwallet.user_service.infrastructure.repository.entity.UserEntity;
import com.simuwallet.user_service.infrastructure.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

// 应用服务层，负责协调领域逻辑、基础设施层和接口层之间的交互。
@Service
public class UserApplicationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserDomainService userDomainService;
    private final PasswordEncryptor passwordEncryptor;
    private final JwtUtil jwtUtil;

    public UserApplicationService(UserRepository userRepository,
                                  UserMapper userMapper,
                                  UserDomainService userDomainService,
                                  PasswordEncryptor passwordEncryptor,
                                  JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userDomainService = userDomainService;
        this.passwordEncryptor = passwordEncryptor;
        this.jwtUtil = jwtUtil;
    }

    // 用户注册
    @Transactional
    public void registerUser(String username, String email, String password) {
        // 检查用户是否存在（可以在调用前完成领域服务检查）
        if (userDomainService.isUsernameDuplicate(username, null)) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userDomainService.isEmailDuplicate(email, null)) {
            throw new IllegalArgumentException("Email already exists.");
        }
        Password encryptedPassword = Password.fromRaw(password, passwordEncryptor);
        User newUser = User.createNew(username, email,encryptedPassword);
        UserEntity userEntity = userMapper.toEntity(newUser);
        userRepository.save(userEntity);
    }

    // 用户登录
    public String loginUser(String email, String rawPassword) {
        UserEntity userEntity = (UserEntity) userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        User user = userMapper.toDomain(userEntity);

        if (!user.verifyPassword(rawPassword, passwordEncryptor)) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getUserId());
    }

    // 验证token
    public User verifyToken(String token) {
        String userId = jwtUtil.parseToken(token).getSubject();

        return userRepository.findByUserId(userId)
                .map(userMapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token or user not found"));
    }

    // 删除用户
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userRepository.delete(userEntity);
    }

    public void updateUserEmail(String userId, String newEmail) {
        // 校验新邮箱是否重复
        if (userDomainService.isEmailDuplicate(newEmail, userId)) {
            throw new IllegalArgumentException("Email already exists.");
        }

        // 从数据库加载用户
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        // 更新密码字段和修改时间
        userEntity.setEmail(newEmail);
        userEntity.setModifiedAt(LocalDateTime.now());

        // 保存更新后的实体
        userRepository.save(userEntity);
    }

    public void updateUserName(String userId, String newUsername) {
        // 校验用户名是否重复
        if (userDomainService.isUsernameDuplicate(newUsername, userId)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        // 从数据库加载用户
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 更新密码字段和修改时间
        userEntity.setUsername(newUsername);
        userEntity.setModifiedAt(LocalDateTime.now());

        // 保存更新后的实体
        userRepository.save(userEntity);
    }

    public void updatePassword(String userId, String rawPassword) {
        // 从数据库加载用户
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String encryptedPassword = passwordEncryptor.encrypt(rawPassword);

        // 更新密码字段和修改时间
        userEntity.setPassword(encryptedPassword);
        userEntity.setModifiedAt(LocalDateTime.now());

        // 保存更新后的实体
        userRepository.save(userEntity);
    }
}
