package com.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cms.common.Result;
import com.cms.entity.Article;
import com.cms.entity.Visit;
import com.cms.mapper.ArticleMapper;
import com.cms.mapper.VisitMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final ArticleMapper articleMapper;
    private final VisitMapper visitMapper;

    public StatisticsController(ArticleMapper articleMapper, VisitMapper visitMapper) {
        this.articleMapper = articleMapper;
        this.visitMapper = visitMapper;
    }

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> result = new HashMap<>();
        
        Long totalArticles = articleMapper.selectCount(null);
        result.put("totalArticles", totalArticles);
        
        Long publishedArticles = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 2));
        result.put("publishedArticles", publishedArticles);
        
        Long pendingArticles = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1));
        result.put("pendingArticles", pendingArticles);
        
        Long totalVisits = visitMapper.selectCount(null);
        result.put("totalVisits", totalVisits);
        
        return Result.success(result);
    }

    @GetMapping("/visit-trend")
    public Result<List<Map<String, Object>>> visitTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        LocalDateTime now = LocalDateTime.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = now.minusDays(i).toLocalDate();
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            
            Long count = visitMapper.selectCount(
                    new LambdaQueryWrapper<Visit>()
                            .ge(Visit::getCreateTime, start)
                            .lt(Visit::getCreateTime, end));
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            result.add(item);
        }
        
        return Result.success(result);
    }

    @GetMapping("/article-trend")
    public Result<List<Map<String, Object>>> articleTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        LocalDateTime now = LocalDateTime.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = now.minusDays(i).toLocalDate();
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            
            Long count = articleMapper.selectCount(
                    new LambdaQueryWrapper<Article>()
                            .ge(Article::getCreateTime, start)
                            .lt(Article::getCreateTime, end));
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            result.add(item);
        }
        
        return Result.success(result);
    }

    @GetMapping("/category-stats")
    public Result<List<Map<String, Object>>> categoryStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        List<Article> articles = articleMapper.selectList(null);
        Map<Long, Long> categoryCount = new HashMap<>();
        for (Article article : articles) {
            if (article.getCategoryId() != null) {
                categoryCount.merge(article.getCategoryId(), 1L, Long::sum);
            }
        }
        
        for (Map.Entry<Long, Long> entry : categoryCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("categoryId", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }
        
        return Result.success(result);
    }
}
