package com.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cms_visit")
public class Visit {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ip;
    private String userAgent;
    private String pageUrl;
    private Long articleId;
    private LocalDateTime createTime;
}
