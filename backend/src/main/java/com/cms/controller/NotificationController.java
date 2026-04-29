package com.cms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.Notification;
import com.cms.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/list")
    public Result<Page<Notification>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer notificationType,
            @RequestParam(required = false) Integer isRead) {
        Page<Notification> result = notificationService.getNotifications(
                userId, pageNum, pageSize, notificationType, isRead);
        return Result.success(result);
    }

    @GetMapping("/unread/count")
    public Result<Map<String, Object>> getUnreadCount(@RequestParam(required = false) Long userId) {
        // 如果userId为空，默认使用当前登录用户（这里简化处理，实际应该从认证中获取）
        if (userId == null) {
            userId = 1L;
        }
        int count = notificationService.getUnreadCount(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Notification> getById(@PathVariable Long id) {
        Notification notification = notificationService.getById(id);
        return Result.success(notification);
    }

    @PostMapping
    public Result<Boolean> send(@RequestBody Notification notification) {
        boolean result = notificationService.sendNotification(notification);
        return Result.success(result);
    }

    @PutMapping("/read/{id}")
    public Result<Boolean> markAsRead(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            userId = 1L;
        }
        boolean result = notificationService.markAsRead(id, userId);
        return Result.success(result);
    }

    @PutMapping("/read-all")
    public Result<Boolean> markAllAsRead(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            userId = 1L;
        }
        boolean result = notificationService.markAllAsRead(userId);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = notificationService.removeById(id);
        return Result.success(result);
    }
}
