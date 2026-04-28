package com.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cms.common.Result;
import com.cms.entity.Site;
import com.cms.mapper.SiteMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/site")
public class SiteController {

    private final SiteMapper siteMapper;

    public SiteController(SiteMapper siteMapper) {
        this.siteMapper = siteMapper;
    }

    @GetMapping("/list")
    public Result<List<Site>> list() {
        LambdaQueryWrapper<Site> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Site::getCreateTime);
        List<Site> list = siteMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Site> getById(@PathVariable Long id) {
        Site site = siteMapper.selectById(id);
        return Result.success(site);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Site site) {
        site.setCreateTime(LocalDateTime.now());
        site.setUpdateTime(LocalDateTime.now());
        int result = siteMapper.insert(site);
        return Result.success(result > 0);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Site site) {
        site.setUpdateTime(LocalDateTime.now());
        int result = siteMapper.updateById(site);
        return Result.success(result > 0);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        int result = siteMapper.deleteById(id);
        return Result.success(result > 0);
    }
}
