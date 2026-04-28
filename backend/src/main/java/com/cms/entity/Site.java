package com.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cms_site")
public class Site {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String domain;
    private String logo;
    private String description;
    private String keywords;
    private String layout;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
