package com.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private Integer notificationType;
    private Long receiverId;
    private String receiverName;
    private Long senderId;
    private String senderName;
    private Integer isRead;
    private LocalDateTime readTime;
    private String relatedType;
    private Long relatedId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
