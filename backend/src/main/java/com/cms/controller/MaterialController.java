package com.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.Material;
import com.cms.mapper.MaterialMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/material")
public class MaterialController {

    private final MaterialMapper materialMapper;

    public MaterialController(MaterialMapper materialMapper) {
        this.materialMapper = materialMapper;
    }

    @GetMapping("/page")
    public Result<Page<Material>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId) {
        
        Page<Material> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(Material::getName, name);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Material::getType, type);
        }
        if (categoryId != null) {
            wrapper.eq(Material::getCategoryId, categoryId);
        }
        
        wrapper.orderByDesc(Material::getCreateTime);
        Page<Material> result = materialMapper.selectPage(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<Material>> list(
            @RequestParam(required = false) String type) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Material::getType, type);
        }
        wrapper.orderByDesc(Material::getCreateTime);
        List<Material> list = materialMapper.selectList(wrapper);
        return Result.success(list);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Material material) {
        material.setCreateTime(LocalDateTime.now());
        material.setUpdateTime(LocalDateTime.now());
        int result = materialMapper.insert(material);
        return Result.success(result > 0);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        int result = materialMapper.deleteById(id);
        return Result.success(result > 0);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Material material) {
        material.setUpdateTime(LocalDateTime.now());
        int result = materialMapper.updateById(material);
        return Result.success(result > 0);
    }
}
