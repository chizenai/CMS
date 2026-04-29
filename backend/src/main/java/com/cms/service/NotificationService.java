package com.cms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cms.entity.Notification;

public interface NotificationService extends IService<Notification> {
    Page<Notification> getNotifications(Long userId, Integer pageNum, Integer pageSize, 
                                         Integer notificationType, Integer isRead);
    boolean markAsRead(Long id, Long userId);
    boolean markAllAsRead(Long userId);
    int getUnreadCount(Long userId);
    boolean sendNotification(Notification notification);
    boolean sendSystemNotification(String title, String content, Integer priority);
    boolean sendAuditNotification(Long receiverId, String title, String content, 
                                   String relatedType, Long relatedId);
}
