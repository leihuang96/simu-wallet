package io.github.leihuang96.user_service.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "\"user\"") // 使用双引号转义表名
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId; // UUID 格式的用户唯一标识符

    @Column(name = "username", unique = true, nullable = false)
    private String username; // 用户名

    @Column(name = "email", unique = true, nullable = false)
    private String email; // 邮箱

    @Column(name = "password", nullable = false)
    private String password; // 加密后的密码

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt; // 修改时间

}
