package com.simuwallet.user_service.controller;

import com.simuwallet.user_service.application.UserApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        if (username == null || email == null || password == null) {
            return ResponseEntity.badRequest().body("Missing required fields.");
        }

        userApplicationService.registerUser(username, email, password);
        return ResponseEntity.ok("User registered successfully.");
    }

    // 用户登录
    @PostMapping("/login")
    public ResponseEntity<String> loginUser (@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String password = requestBody.get("password");
        String token = userApplicationService.loginUser(email, password);
        return ResponseEntity.ok(token);
    }

    // 删除用户
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        userApplicationService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }


    // 更新用户邮箱
    @PutMapping("/{userId}/email")
    public ResponseEntity<String> updateUserEmail(@PathVariable String userId, @RequestBody Map<String, String> requestBody) {
        String newEmail = requestBody.get("newEmail");
        userApplicationService.updateUserEmail(userId, newEmail);
        return ResponseEntity.ok("Email updated successfully.");
    }

    // 更新用户密码
    @PutMapping("/{userId}/password")
    public ResponseEntity<String> updatePassword(@PathVariable String userId, @RequestBody Map<String, String> requestBody) {
        String newPassword = requestBody.get("password");
        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Password cannot be null or empty.");
        }
        userApplicationService.updatePassword(userId, newPassword);
        return ResponseEntity.ok("Password updated successfully.");
    }

    // 更新用户名
    @PutMapping("/{userId}/username")
    public ResponseEntity<String> updateUserName(@PathVariable String userId, @RequestBody Map<String, String> requestBody) {
        String newUsername = requestBody.get("newUsername");
        userApplicationService.updateUserName(userId, newUsername);
        return ResponseEntity.ok("Username updated successfully.");
    }

}
