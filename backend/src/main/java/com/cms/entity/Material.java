package com.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cms_material")
public class Material {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;
    private String url;
    private String thumbnail;
    private Long size;
    private String description;
    private Long categoryId;
    private Long uploaderId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String categoryName;
    @TableField(exist = false)
    private String uploaderName;
}
