package com.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.Category;
import com.cms.entity.Material;
import com.cms.mapper.CategoryMapper;
import com.cms.mapper.MaterialMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/material")
public class MaterialController {

    private final MaterialMapper materialMapper;
    private final CategoryMapper categoryMapper;

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @Value("${file.access.prefix:/uploads/}")
    private String accessPrefix;

    public MaterialController(MaterialMapper materialMapper, CategoryMapper categoryMapper) {
        this.materialMapper = materialMapper;
        this.categoryMapper = categoryMapper;
    }

    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            Path filePath = uploadDir.resolve(newFileName);
            file.transferTo(filePath);
            
            String fileType = getFileType(fileExtension);
            String fileUrl = accessPrefix + newFileName;
            
            Material material = new Material();
            material.setName(originalFilename);
            material.setType(fileType);
            material.setUrl(fileUrl);
            material.setSize(file.getSize());
            material.setCreateTime(LocalDateTime.now());
            material.setUpdateTime(LocalDateTime.now());
            materialMapper.insert(material);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", material.getId());
            result.put("name", originalFilename);
            result.put("url", fileUrl);
            result.put("type", fileType);
            result.put("size", file.getSize());
            
            return Result.success(result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    private String getFileType(String extension) {
        String ext = extension.toLowerCase();
        if (ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") || 
            ext.equals(".gif") || ext.equals(".bmp") || ext.equals(".webp")) {
            return "image";
        } else if (ext.equals(".mp4") || ext.equals(".avi") || ext.equals(".mov") || 
                   ext.equals(".mkv") || ext.equals(".wmv")) {
            return "video";
        } else if (ext.equals(".mp3") || ext.equals(".wav") || ext.equals(".flac") || 
                   ext.equals(".aac")) {
            return "audio";
        } else {
            return "file";
        }
    }

    private void setCategoryName(List<Material> materials) {
        Set<Long> categoryIds = materials.stream()
                .map(Material::getCategoryId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        
        if (!categoryIds.isEmpty()) {
            List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
            Map<Long, String> categoryMap = categories.stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
            
            materials.forEach(material -> {
                if (material.getCategoryId() != null) {
                    material.setCategoryName(categoryMap.get(material.getCategoryId()));
                }
            });
        }
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
        setCategoryName(result.getRecords());
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
        setCategoryName(list);
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
