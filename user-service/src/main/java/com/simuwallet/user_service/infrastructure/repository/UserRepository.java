package com.simuwallet.user_service.infrastructure.repository;

import com.simuwallet.user_service.infrastructure.repository.entity.UserEntity;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// UserRepository 是一个 JPA 仓库接口，主要负责与数据库交互。它定义了用于查询用户的特定方法。
@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END " +
            "FROM UserEntity u WHERE u.username = :username AND u.userId != :userId")
    boolean existsByUsernameAndNotUserId(@Param("username") String username, @Param("userId") String userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END " +
            "FROM UserEntity u WHERE u.email = :email AND u.userId != :userId")
    boolean existsByEmailAndNotUserId(@Param("email") String email, @Param("userId") String userId);

    Optional<Object> findByEmail(String email);

    Optional<UserEntity> findByUserId(String userId);
}
