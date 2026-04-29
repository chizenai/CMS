package com.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cms_audit_record")
public class AuditRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private Long submitterId;
    private String submitterName;
    private LocalDateTime submitTime;
    private Long auditorId;
    private String auditorName;
    private LocalDateTime auditTime;
    private Integer auditStatus;
    private String auditComment;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
