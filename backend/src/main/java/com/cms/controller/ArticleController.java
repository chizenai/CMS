package com.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.Article;
import com.cms.entity.Category;
import com.cms.mapper.ArticleMapper;
import com.cms.mapper.CategoryMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;

    public ArticleController(ArticleMapper articleMapper, CategoryMapper categoryMapper) {
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/page")
    public Result<Page<Article>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        
        if (title != null && !title.isEmpty()) {
            wrapper.like(Article::getTitle, title);
        }
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Article::getStatus, status);
        }
        
        wrapper.orderByDesc(Article::getCreateTime);
        Page<Article> result = articleMapper.selectPage(page, wrapper);
        
        Set<Long> categoryIds = result.getRecords().stream()
                .map(Article::getCategoryId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        
        if (!categoryIds.isEmpty()) {
            List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
            Map<Long, String> categoryMap = categories.stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
            
            result.getRecords().forEach(article -> {
                if (article.getCategoryId() != null) {
                    article.setCategoryName(categoryMap.get(article.getCategoryId()));
                }
            });
        }
        
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Article> getById(@PathVariable Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null && article.getViewCount() == null) {
            article.setViewCount(0);
        }
        return Result.success(article);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Article article) {
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setViewCount(0);
        int result = articleMapper.insert(article);
        return Result.success(result > 0);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Article article) {
        article.setUpdateTime(LocalDateTime.now());
        int result = articleMapper.updateById(article);
        return Result.success(result > 0);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        int result = articleMapper.deleteById(id);
        return Result.success(result > 0);
    }

    @PutMapping("/publish/{id}")
    public Result<Boolean> publish(@PathVariable Long id) {
        Article existingArticle = articleMapper.selectById(id);
        if (existingArticle == null) {
            return Result.error("文章不存在");
        }
        if (existingArticle.getStatus() != 1) {
            return Result.error("只有待审核状态的文章才能发布");
        }
        Article article = new Article();
        article.setId(id);
        article.setStatus(2);
        article.setPublishTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        int result = articleMapper.updateById(article);
        return Result.success(result > 0);
    }

    @PutMapping("/revoke/{id}")
    public Result<Boolean> revoke(@PathVariable Long id) {
        Article existingArticle = articleMapper.selectById(id);
        if (existingArticle == null) {
            return Result.error("文章不存在");
        }
        if (existingArticle.getStatus() != 2 && existingArticle.getStatus() != 3) {
            return Result.error("只有已发布或已拒绝状态的文章才能撤销");
        }
        Article article = new Article();
        article.setId(id);
        article.setStatus(0);
        article.setUpdateTime(LocalDateTime.now());
        int result = articleMapper.updateById(article);
        return Result.success(result > 0);
    }

    @PutMapping("/audit")
    public Result<Boolean> audit(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        String comment = (String) params.get("comment");

        Article existingArticle = articleMapper.selectById(id);
        if (existingArticle == null) {
            return Result.error("文章不存在");
        }
        if (existingArticle.getStatus() != 1) {
            return Result.error("只有待审核状态的文章才能审核");
        }

        Article article = new Article();
        article.setId(id);
        article.setStatus(status);
        article.setAuditComment(comment);
        article.setAuditTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        if (status == 2) {
            article.setPublishTime(LocalDateTime.now());
        }
        int result = articleMapper.updateById(article);
        return Result.success(result > 0);
    }
}
