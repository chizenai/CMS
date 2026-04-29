package com.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.User;
import com.cms.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/page")
    public Result<Page<User>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(User::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(page, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<User>> list() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreateTime);
        List<User> list = userMapper.selectList(wrapper);
        list.forEach(u -> u.setPassword(null));
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody User user) {
        User existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        int result = userMapper.insert(user);
        return Result.success(result > 0);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody User user) {
        user.setUpdateTime(LocalDateTime.now());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        int result = userMapper.updateById(user);
        return Result.success(result > 0);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        int result = userMapper.deleteById(id);
        return Result.success(result > 0);
    }

    @PutMapping("/reset-password/{id}")
    public Result<Boolean> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String newPassword = params.get("newPassword");
        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        int result = userMapper.updateById(user);
        return Result.success(result > 0);
    }

    @GetMapping("/profile/info")
    public Result<User> getProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PutMapping("/profile/info")
    public Result<Boolean> updateProfile(Authentication authentication, @RequestBody User user) {
        String username = authentication.getName();
        User existingUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        User updateUser = new User();
        updateUser.setId(existingUser.getId());
        
        if (user.getNickname() != null) {
            updateUser.setNickname(user.getNickname());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }
        if (user.getPhone() != null) {
            updateUser.setPhone(user.getPhone());
        }
        if (user.getAvatar() != null) {
            updateUser.setAvatar(user.getAvatar());
        }
        
        updateUser.setUpdateTime(LocalDateTime.now());
        int result = userMapper.updateById(updateUser);
        return Result.success(result > 0);
    }

    @PutMapping("/profile/password")
    public Result<Boolean> updatePassword(Authentication authentication, @RequestBody Map<String, String> params) {
        String username = authentication.getName();
        User existingUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            return Result.error("密码不能为空");
        }
        
        boolean passwordValid = false;
        if (existingUser.getPassword().startsWith("$2a$") || existingUser.getPassword().startsWith("$2b$")) {
            passwordValid = passwordEncoder.matches(oldPassword, existingUser.getPassword());
        } else {
            passwordValid = oldPassword.equals(existingUser.getPassword());
        }
        
        if (!passwordValid) {
            return Result.error("原密码错误");
        }
        
        User updateUser = new User();
        updateUser.setId(existingUser.getId());
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        updateUser.setUpdateTime(LocalDateTime.now());
        
        int result = userMapper.updateById(updateUser);
        return Result.success(result > 0);
    }
}
