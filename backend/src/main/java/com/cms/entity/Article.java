package com.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cms_article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String summary;
    private String cover;
    private String content;
    private Long categoryId;
    private Integer type;
    private Long authorId;
    private Integer status;
    private Integer viewCount;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime publishTime;
    private Long auditorId;
    private LocalDateTime auditTime;
    private String auditComment;

    @TableField(exist = false)
    private String categoryName;
    @TableField(exist = false)
    private String authorName;
    @TableField(exist = false)
    private String auditorName;
}
