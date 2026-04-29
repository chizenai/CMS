package com.cms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.AuditRecord;
import com.cms.service.AuditRecordService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditRecordService auditRecordService;

    public AuditController(AuditRecordService auditRecordService) {
        this.auditRecordService = auditRecordService;
    }

    @GetMapping("/queue")
    public Result<Page<AuditRecord>> getAuditQueue(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId) {
        Page<AuditRecord> result = auditRecordService.getAuditQueue(pageNum, pageSize, title, categoryId);
        return Result.success(result);
    }

    @GetMapping("/history")
    public Result<Page<AuditRecord>> getAuditHistory(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long articleId,
            @RequestParam(required = false) Long submitterId,
            @RequestParam(required = false) Long auditorId,
            @RequestParam(required = false) Integer auditStatus) {
        Page<AuditRecord> result = auditRecordService.getAuditHistory(
                pageNum, pageSize, articleId, submitterId, auditorId, auditStatus);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<AuditRecord> getById(@PathVariable Long id) {
        AuditRecord auditRecord = auditRecordService.getById(id);
        return Result.success(auditRecord);
    }

    @PutMapping
    public Result<Boolean> audit(@RequestBody Map<String, Object> params, Authentication authentication) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer auditStatus = Integer.valueOf(params.get("auditStatus").toString());
        String auditComment = (String) params.get("auditComment");
        
        // 从认证中获取用户信息
        // 这里简化处理，实际应该从UserDetails中获取
        Long auditorId = 1L; // 默认管理员ID，实际应该从authentication中获取
        String auditorName = "管理员"; // 实际应该从authentication中获取
        
        boolean result = auditRecordService.auditArticle(id, auditorId, auditorName, auditStatus, auditComment);
        return result ? Result.success(true) : Result.error("审核失败");
    }

    @PostMapping("/batch")
    public Result<Boolean> batchAudit(@RequestBody Map<String, Object> params, Authentication authentication) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        Integer auditStatus = Integer.valueOf(params.get("auditStatus").toString());
        String auditComment = (String) params.get("auditComment");
        
        Long auditorId = 1L;
        String auditorName = "管理员";
        
        boolean result = auditRecordService.batchAudit(ids, auditorId, auditorName, auditStatus, auditComment);
        return Result.success(result);
    }
}
