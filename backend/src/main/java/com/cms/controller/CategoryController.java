package com.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cms.common.Result;
import com.cms.entity.Category;
import com.cms.mapper.CategoryMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/list")
    public Result<List<Category>> list() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        List<Category> list = categoryMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/tree")
    public Result<List<Category>> tree() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        List<Category> list = categoryMapper.selectList(wrapper);
        return Result.success(buildTree(list, 0L));
    }

    private List<Category> buildTree(List<Category> categories, Long parentId) {
        return categories.stream()
                .filter(c -> parentId.equals(c.getParentId()))
                .peek(c -> c.setChildren(buildTree(categories, c.getId())))
                .toList();
    }

    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        Category category = categoryMapper.selectById(id);
        return Result.success(category);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getLevel() == null) {
            category.setLevel(1);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        int result = categoryMapper.insert(category);
        return Result.success(result > 0);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Category category) {
        category.setUpdateTime(LocalDateTime.now());
        int result = categoryMapper.updateById(category);
        return Result.success(result > 0);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        int result = categoryMapper.deleteById(id);
        return Result.success(result > 0);
    }

    @PostMapping("/sort")
    public Result<Boolean> updateSort(@RequestBody List<Category> categories) {
        for (Category category : categories) {
            Category update = new Category();
            update.setId(category.getId());
            update.setSort(category.getSort());
            update.setParentId(category.getParentId());
            categoryMapper.updateById(update);
        }
        return Result.success(true);
    }
}
