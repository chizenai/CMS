package com.cms.controller;

import com.cms.common.Result;
import com.cms.entity.User;
import com.cms.mapper.UserMapper;
import com.cms.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserService userService, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public Result<User> getProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PutMapping
    public Result<Boolean> updateProfile(@RequestBody User user, Authentication authentication) {
        String username = authentication.getName();
        User existingUser = userService.getByUsername(username);
        
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        // 只允许更新部分字段
        User updateUser = new User();
        updateUser.setId(existingUser.getId());
        updateUser.setNickname(user.getNickname());
        updateUser.setAvatar(user.getAvatar());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setUpdateTime(LocalDateTime.now());
        
        int result = userMapper.updateById(updateUser);
        return Result.success(result > 0);
    }

    @PutMapping("/change-password")
    public Result<Boolean> changePassword(@RequestBody Map<String, String> params, Authentication authentication) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            return Result.error("密码不能为空");
        }
        
        String username = authentication.getName();
        User user = userService.getByUsername(username);
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 验证旧密码
        boolean passwordValid = false;
        if (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$")) {
            passwordValid = passwordEncoder.matches(oldPassword, user.getPassword());
        } else {
            passwordValid = oldPassword.equals(user.getPassword());
        }
        
        if (!passwordValid) {
            return Result.error("原密码错误");
        }
        
        // 更新密码
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        updateUser.setUpdateTime(LocalDateTime.now());
        
        int result = userMapper.updateById(updateUser);
        return Result.success(result > 0);
    }
}
