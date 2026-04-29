package com.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cms.entity.Article;
import com.cms.entity.AuditRecord;
import com.cms.entity.Category;
import com.cms.mapper.ArticleMapper;
import com.cms.mapper.AuditRecordMapper;
import com.cms.mapper.CategoryMapper;
import com.cms.service.AuditRecordService;
import com.cms.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuditRecordServiceImpl extends ServiceImpl<AuditRecordMapper, AuditRecord> implements AuditRecordService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final NotificationService notificationService;

    public AuditRecordServiceImpl(ArticleMapper articleMapper, 
                                   CategoryMapper categoryMapper,
                                   NotificationService notificationService) {
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
        this.notificationService = notificationService;
    }

    @Override
    public Page<AuditRecord> getAuditQueue(Integer pageNum, Integer pageSize, String title, Long categoryId) {
        Page<AuditRecord> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AuditRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuditRecord::getAuditStatus, 0)
               .orderByDesc(AuditRecord::getSubmitTime);
        
        Page<AuditRecord> result = this.page(page, wrapper);
        
        // 补充文章信息
        Set<Long> articleIds = result.getRecords().stream()
                .map(AuditRecord::getArticleId)
                .collect(Collectors.toSet());
        
        if (!articleIds.isEmpty()) {
            List<Article> articles = articleMapper.selectBatchIds(articleIds);
            Map<Long, Article> articleMap = articles.stream()
                    .collect(Collectors.toMap(Article::getId, a -> a));
            
            Set<Long> categoryIds = articles.stream()
                    .map(Article::getCategoryId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
            
            Map<Long, String> categoryMap = null;
            if (!categoryIds.isEmpty()) {
                List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
                categoryMap = categories.stream()
                        .collect(Collectors.toMap(Category::getId, Category::getName));
            }
            
            final Map<Long, String> finalCategoryMap = categoryMap;
            result.getRecords().forEach(record -> {
                Article article = articleMap.get(record.getArticleId());
                if (article != null) {
                    record.setArticleTitle(article.getTitle());
                    if (finalCategoryMap != null && article.getCategoryId() != null) {
                        record.setCategoryName(finalCategoryMap.get(article.getCategoryId()));
                    }
                }
            });
        }
        
        return result;
    }

    @Override
    public Page<AuditRecord> getAuditHistory(Integer pageNum, Integer pageSize, Long articleId, 
                                               Long submitterId, Long auditorId, Integer auditStatus) {
        Page<AuditRecord> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AuditRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (articleId != null) {
            wrapper.eq(AuditRecord::getArticleId, articleId);
        }
        if (submitterId != null) {
            wrapper.eq(AuditRecord::getSubmitterId, submitterId);
        }
        if (auditorId != null) {
            wrapper.eq(AuditRecord::getAuditorId, auditorId);
        }
        if (auditStatus != null) {
            wrapper.eq(AuditRecord::getAuditStatus, auditStatus);
        }
        
        wrapper.ne(AuditRecord::getAuditStatus, 0)
               .orderByDesc(AuditRecord::getAuditTime);
        
        Page<AuditRecord> result = this.page(page, wrapper);
        
        // 补充文章信息
        Set<Long> articleIds = result.getRecords().stream()
                .map(AuditRecord::getArticleId)
                .collect(Collectors.toSet());
        
        if (!articleIds.isEmpty()) {
            List<Article> articles = articleMapper.selectBatchIds(articleIds);
            Map<Long, Article> articleMap = articles.stream()
                    .collect(Collectors.toMap(Article::getId, a -> a));
            
            Set<Long> categoryIds = articles.stream()
                    .map(Article::getCategoryId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
            
            Map<Long, String> categoryMap = null;
            if (!categoryIds.isEmpty()) {
                List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
                categoryMap = categories.stream()
                        .collect(Collectors.toMap(Category::getId, Category::getName));
            }
            
            final Map<Long, String> finalCategoryMap = categoryMap;
            result.getRecords().forEach(record -> {
                Article article = articleMap.get(record.getArticleId());
                if (article != null) {
                    record.setArticleTitle(article.getTitle());
                    if (finalCategoryMap != null && article.getCategoryId() != null) {
                        record.setCategoryName(finalCategoryMap.get(article.getCategoryId()));
                    }
                }
            });
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean createAuditRecord(AuditRecord auditRecord) {
        auditRecord.setSubmitTime(LocalDateTime.now());
        auditRecord.setAuditStatus(0);
        auditRecord.setVersion(1);
        auditRecord.setCreateTime(LocalDateTime.now());
        auditRecord.setUpdateTime(LocalDateTime.now());
        return this.save(auditRecord);
    }

    @Override
    @Transactional
    public boolean auditArticle(Long auditRecordId, Long auditorId, String auditorName, 
                                 Integer auditStatus, String auditComment) {
        AuditRecord auditRecord = this.getById(auditRecordId);
        if (auditRecord == null || auditRecord.getAuditStatus() != 0) {
            return false;
        }
        
        // 更新审核记录
        auditRecord.setAuditorId(auditorId);
        auditRecord.setAuditorName(auditorName);
        auditRecord.setAuditTime(LocalDateTime.now());
        auditRecord.setAuditStatus(auditStatus);
        auditRecord.setAuditComment(auditComment);
        auditRecord.setUpdateTime(LocalDateTime.now());
        this.updateById(auditRecord);
        
        // 更新文章状态
        Article article = new Article();
        article.setId(auditRecord.getArticleId());
        article.setStatus(auditStatus == 1 ? 2 : 3); // 1通过->已发布(2)，2拒绝->已拒绝(3)
        article.setAuditorId(auditorId);
        article.setAuditTime(LocalDateTime.now());
        article.setAuditComment(auditComment);
        article.setUpdateTime(LocalDateTime.now());
        if (auditStatus == 1) {
            article.setPublishTime(LocalDateTime.now());
        }
        articleMapper.updateById(article);
        
        // 发送通知
        String statusText = auditStatus == 1 ? "通过" : "拒绝";
        String title = "文章审核" + statusText + "通知";
        String content = "您提交的文章《" + articleMapper.selectById(auditRecord.getArticleId()).getTitle() + 
                         "》已被" + statusText + "。" + 
                         (auditComment != null ? "审核意见：" + auditComment : "");
        notificationService.sendAuditNotification(
                auditRecord.getSubmitterId(), 
                title, 
                content, 
                "article", 
                auditRecord.getArticleId()
        );
        
        return true;
    }

    @Override
    @Transactional
    public boolean batchAudit(List<Long> auditRecordIds, Long auditorId, String auditorName,
                               Integer auditStatus, String auditComment) {
        for (Long id : auditRecordIds) {
            auditArticle(id, auditorId, auditorName, auditStatus, auditComment);
        }
        return true;
    }
}
