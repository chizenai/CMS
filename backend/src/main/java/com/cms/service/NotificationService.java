package com.cms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.entity.Notification;
import com.cms.mapper.NotificationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public Page<Notification> getNotificationPage(Integer pageNum, Integer pageSize, 
                                                    Long receiverId, Integer notificationType, 
                                                    Integer isRead) {
        Page<Notification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        
        if (receiverId != null) {
            wrapper.eq(Notification::getReceiverId, receiverId);
        }
        if (notificationType != null) {
            wrapper.eq(Notification::getNotificationType, notificationType);
        }
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead);
        }
        
        wrapper.orderByDesc(Notification::getCreateTime);
        return notificationMapper.selectPage(page, wrapper);
    }

    public List<Notification> getUnreadNotifications(Long receiverId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getReceiverId, receiverId)
               .eq(Notification::getIsRead, 0)
               .orderByDesc(Notification::getCreateTime);
        return notificationMapper.selectList(wrapper);
    }

    public Long getUnreadCount(Long receiverId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getReceiverId, receiverId)
               .eq(Notification::getIsRead, 0);
        return notificationMapper.selectCount(wrapper);
    }

    public Notification getById(Long id) {
        return notificationMapper.selectById(id);
    }

    public boolean save(Notification notification) {
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        if (notification.getIsRead() == null) {
            notification.setIsRead(0);
        }
        return notificationMapper.insert(notification) > 0;
    }

    @Transactional
    public boolean markAsRead(Long id) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            return false;
        }
        notification.setIsRead(1);
        notification.setReadTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        return notificationMapper.updateById(notification) > 0;
    }

    @Transactional
    public boolean markAllAsRead(Long receiverId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getReceiverId, receiverId)
               .eq(Notification::getIsRead, 0);
        
        List<Notification> unreadList = notificationMapper.selectList(wrapper);
        for (Notification notification : unreadList) {
            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
            notification.setUpdateTime(LocalDateTime.now());
            notificationMapper.updateById(notification);
        }
        return true;
    }

    public boolean delete(Long id) {
        return notificationMapper.deleteById(id) > 0;
    }

    public Notification createSystemNotification(String title, String content, Long receiverId, String receiverName) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotificationType(1);
        notification.setReceiverId(receiverId);
        notification.setReceiverName(receiverName);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
        return notification;
    }

    public Notification createAuditNotification(String title, String content, Long receiverId, 
                                                  String receiverName, String relatedType, Long relatedId) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotificationType(2);
        notification.setReceiverId(receiverId);
        notification.setReceiverName(receiverName);
        notification.setRelatedType(relatedType);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
        return notification;
    }

    public Notification createTaskNotification(String title, String content, Long receiverId, 
                                                 String receiverName, String relatedType, Long relatedId) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotificationType(3);
        notification.setReceiverId(receiverId);
        notification.setReceiverName(receiverName);
        notification.setRelatedType(relatedType);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
        return notification;
    }
}
