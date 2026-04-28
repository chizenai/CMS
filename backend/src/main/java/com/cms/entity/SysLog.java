package com.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_log")
public class SysLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String module;
    private String operation;
    private String method;
    private String params;
    private Long userId;
    private String username;
    private String ip;
    private Integer status;
    private String errorMsg;
    private Long time;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String timeStr;
}
