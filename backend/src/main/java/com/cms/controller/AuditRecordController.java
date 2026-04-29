package com.cms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.AuditRecord;
import com.cms.service.AuditRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit-record")
public class AuditRecordController {

    private final AuditRecordService auditRecordService;

    public AuditRecordController(AuditRecordService auditRecordService) {
        this.auditRecordService = auditRecordService;
    }

    @GetMapping("/page")
    public Result<Page<AuditRecord>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long articleId,
            @RequestParam(required = false) Integer auditStatus) {
        Page<AuditRecord> result = auditRecordService.getRecordPage(pageNum, pageSize, articleId, auditStatus);
        return Result.success(result);
    }

    @GetMapping("/article/{articleId}")
    public Result<List<AuditRecord>> getByArticleId(@PathVariable Long articleId) {
        List<AuditRecord> records = auditRecordService.getRecordsByArticleId(articleId);
        return Result.success(records);
    }

    @GetMapping("/{id}")
    public Result<AuditRecord> getById(@PathVariable Long id) {
        AuditRecord record = auditRecordService.getById(id);
        return Result.success(record);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody AuditRecord auditRecord) {
        boolean result = auditRecordService.save(auditRecord);
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody AuditRecord auditRecord) {
        boolean result = auditRecordService.update(auditRecord);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = auditRecordService.delete(id);
        return Result.success(result);
    }

    @PostMapping("/submit")
    public Result<AuditRecord> submit(@RequestBody Map<String, Object> params) {
        Long articleId = Long.valueOf(params.get("articleId").toString());
        Long submitterId = params.containsKey("submitterId") ? Long.valueOf(params.get("submitterId").toString()) : null;
        String submitterName = (String) params.get("submitterName");
        AuditRecord record = auditRecordService.createSubmitRecord(articleId, submitterId, submitterName);
        return Result.success(record);
    }

    @PostMapping("/audit")
    public Result<AuditRecord> audit(@RequestBody Map<String, Object> params) {
        Long recordId = Long.valueOf(params.get("recordId").toString());
        Long auditorId = params.containsKey("auditorId") ? Long.valueOf(params.get("auditorId").toString()) : null;
        String auditorName = (String) params.get("auditorName");
        Integer auditStatus = Integer.valueOf(params.get("auditStatus").toString());
        String auditComment = (String) params.get("auditComment");
        AuditRecord record = auditRecordService.updateAuditResult(recordId, auditorId, auditorName, auditStatus, auditComment);
        return Result.success(record);
    }
}
