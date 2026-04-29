package com.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.Article;
import com.cms.entity.AuditRecord;
import com.cms.entity.Category;
import com.cms.entity.User;
import com.cms.mapper.ArticleMapper;
import com.cms.mapper.CategoryMapper;
import com.cms.service.AuditRecordService;
import com.cms.service.UserService;
import org.springframework.security.core.Authentication;
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
    private final AuditRecordService auditRecordService;
    private final UserService userService;

    public ArticleController(ArticleMapper articleMapper, CategoryMapper categoryMapper,
                             AuditRecordService auditRecordService, UserService userService) {
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
        this.auditRecordService = auditRecordService;
        this.userService = userService;
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
                    String categoryName = categoryMap.get(article.getCategoryId());
                    article.setCategoryName(categoryName != null ? categoryName : "-");
                } else {
                    article.setCategoryName("-");
                }
            });
        } else {
            result.getRecords().forEach(article -> {
                article.setCategoryName("-");
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

    @PutMapping("/submit/{id}")
    public Result<Boolean> submitForAudit(@PathVariable Long id, Authentication authentication) {
        Article existingArticle = articleMapper.selectById(id);
        if (existingArticle == null) {
            return Result.error("文章不存在");
        }
        if (existingArticle.getStatus() != 0) {
            return Result.error("只有草稿状态的文章才能提交审核");
        }

        // 更新文章状态为待审核
        Article article = new Article();
        article.setId(id);
        article.setStatus(1);
        article.setUpdateTime(LocalDateTime.now());
        int result = articleMapper.updateById(article);

        if (result > 0) {
            // 获取当前用户信息
            String username = authentication.getName();
            User submitter = userService.getByUsername(username);
            
            // 创建审核记录
            AuditRecord auditRecord = new AuditRecord();
            auditRecord.setArticleId(id);
            if (submitter != null) {
                auditRecord.setSubmitterId(submitter.getId());
                auditRecord.setSubmitterName(submitter.getNickname() != null ? submitter.getNickname() : submitter.getUsername());
            } else {
                // 默认为系统用户
                auditRecord.setSubmitterId(1L);
                auditRecord.setSubmitterName("系统用户");
            }
            auditRecordService.createAuditRecord(auditRecord);
        }

        return Result.success(result > 0);
    }

    @PutMapping("/withdraw/{id}")
    public Result<Boolean> withdrawFromAudit(@PathVariable Long id) {
        Article existingArticle = articleMapper.selectById(id);
        if (existingArticle == null) {
            return Result.error("文章不存在");
        }
        if (existingArticle.getStatus() != 1) {
            return Result.error("只有待审核状态的文章才能撤回");
        }

        Article article = new Article();
        article.setId(id);
        article.setStatus(0);
        article.setUpdateTime(LocalDateTime.now());
        int result = articleMapper.updateById(article);

        return Result.success(result > 0);
    }
}
