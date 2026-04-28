package com.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.SysLog;
import com.cms.mapper.SysLogMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/log")
public class SysLogController {

    private final SysLogMapper sysLogMapper;

    public SysLogController(SysLogMapper sysLogMapper) {
        this.sysLogMapper = sysLogMapper;
    }

    @GetMapping("/page")
    public Result<Page<SysLog>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String username) {
        
        Page<SysLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        
        if (module != null && !module.isEmpty()) {
            wrapper.like(SysLog::getModule, module);
        }
        if (status != null) {
            wrapper.eq(SysLog::getStatus, status);
        }
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysLog::getUsername, username);
        }
        
        wrapper.orderByDesc(SysLog::getCreateTime);
        Page<SysLog> result = sysLogMapper.selectPage(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<SysLog>> list() {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysLog::getCreateTime)
                .last("LIMIT 100");
        List<SysLog> list = sysLogMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<SysLog> getById(@PathVariable Long id) {
        SysLog log = sysLogMapper.selectById(id);
        return Result.success(log);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        int result = sysLogMapper.deleteById(id);
        return Result.success(result > 0);
    }

    @DeleteMapping("/clear")
    public Result<Boolean> clear() {
        sysLogMapper.delete(null);
        return Result.success(true);
    }
}
