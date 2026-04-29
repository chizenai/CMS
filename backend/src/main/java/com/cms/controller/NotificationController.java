package com.cms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.Notification;
import com.cms.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/page")
    public Result<Page<Notification>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long receiverId,
            @RequestParam(required = false) Integer notificationType,
            @RequestParam(required = false) Integer isRead) {
        Page<Notification> result = notificationService.getNotificationPage(pageNum, pageSize, receiverId, notificationType, isRead);
        return Result.success(result);
    }

    @GetMapping("/unread")
    public Result<List<Notification>> getUnread(@RequestParam(required = false) Long receiverId) {
        if (receiverId == null) {
            receiverId = 1L;
        }
        List<Notification> notifications = notificationService.getUnreadNotifications(receiverId);
        return Result.success(notifications);
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Long>> getUnreadCount(@RequestParam(required = false) Long receiverId) {
        if (receiverId == null) {
            receiverId = 1L;
        }
        Long count = notificationService.getUnreadCount(receiverId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Notification> getById(@PathVariable Long id) {
        Notification notification = notificationService.getById(id);
        return Result.success(notification);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Notification notification) {
        boolean result = notificationService.save(notification);
        return Result.success(result);
    }

    @PutMapping("/read/{id}")
    public Result<Boolean> markAsRead(@PathVariable Long id) {
        boolean result = notificationService.markAsRead(id);
        return Result.success(result);
    }

    @PutMapping("/read-all")
    public Result<Boolean> markAllAsRead(@RequestParam(required = false) Long receiverId) {
        if (receiverId == null) {
            receiverId = 1L;
        }
        boolean result = notificationService.markAllAsRead(receiverId);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = notificationService.delete(id);
        return Result.success(result);
    }

    @PostMapping("/system")
    public Result<Notification> createSystemNotification(@RequestBody Map<String, Object> params) {
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        Long receiverId = params.containsKey("receiverId") ? Long.valueOf(params.get("receiverId").toString()) : null;
        String receiverName = (String) params.get("receiverName");
        Notification notification = notificationService.createSystemNotification(title, content, receiverId, receiverName);
        return Result.success(notification);
    }

    @PostMapping("/audit")
    public Result<Notification> createAuditNotification(@RequestBody Map<String, Object> params) {
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        Long receiverId = params.containsKey("receiverId") ? Long.valueOf(params.get("receiverId").toString()) : null;
        String receiverName = (String) params.get("receiverName");
        String relatedType = (String) params.get("relatedType");
        Long relatedId = params.containsKey("relatedId") ? Long.valueOf(params.get("relatedId").toString()) : null;
        Notification notification = notificationService.createAuditNotification(title, content, receiverId, receiverName, relatedType, relatedId);
        return Result.success(notification);
    }

    @PostMapping("/task")
    public Result<Notification> createTaskNotification(@RequestBody Map<String, Object> params) {
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        Long receiverId = params.containsKey("receiverId") ? Long.valueOf(params.get("receiverId").toString()) : null;
        String receiverName = (String) params.get("receiverName");
        String relatedType = (String) params.get("relatedType");
        Long relatedId = params.containsKey("relatedId") ? Long.valueOf(params.get("relatedId").toString()) : null;
        Notification notification = notificationService.createTaskNotification(title, content, receiverId, receiverName, relatedType, relatedId);
        return Result.success(notification);
    }
}
