package com.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cms.entity.Notification;
import com.cms.mapper.NotificationMapper;
import com.cms.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public Page<Notification> getNotifications(Long userId, Integer pageNum, Integer pageSize, 
                                                 Integer notificationType, Integer isRead) {
        Page<Notification> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Notification::getReceiverId, userId).or().isNull(Notification::getReceiverId));
        
        if (notificationType != null) {
            wrapper.eq(Notification::getNotificationType, notificationType);
        }
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead);
        }
        
        wrapper.orderByDesc(Notification::getPriority)
               .orderByDesc(Notification::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    @Transactional
    public boolean markAsRead(Long id, Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getId, id)
               .eq(Notification::getIsRead, 0)
               .and(w -> w.eq(Notification::getReceiverId, userId).or().isNull(Notification::getReceiverId))
               .set(Notification::getIsRead, 1)
               .set(Notification::getReadTime, LocalDateTime.now());
        return this.update(wrapper);
    }

    @Override
    @Transactional
    public boolean markAllAsRead(Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getIsRead, 0)
               .and(w -> w.eq(Notification::getReceiverId, userId).or().isNull(Notification::getReceiverId))
               .set(Notification::getIsRead, 1)
               .set(Notification::getReadTime, LocalDateTime.now());
        return this.update(wrapper);
    }

    @Override
    public int getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getIsRead, 0)
               .and(w -> w.eq(Notification::getReceiverId, userId).or().isNull(Notification::getReceiverId));
        return (int) this.count(wrapper);
    }

    @Override
    @Transactional
    public boolean sendNotification(Notification notification) {
        notification.setCreateTime(LocalDateTime.now());
        notification.setIsRead(0);
        return this.save(notification);
    }

    @Override
    public boolean sendSystemNotification(String title, String content, Integer priority) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotificationType(1);
        notification.setPriority(priority != null ? priority : 1);
        notification.setSenderName("系统管理员");
        return sendNotification(notification);
    }

    @Override
    public boolean sendAuditNotification(Long receiverId, String title, String content, 
                                           String relatedType, Long relatedId) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotificationType(2);
        notification.setReceiverId(receiverId);
        notification.setPriority(2);
        notification.setSenderName("审核系统");
        notification.setRelatedType(relatedType);
        notification.setRelatedId(relatedId);
        return sendNotification(notification);
    }
}
