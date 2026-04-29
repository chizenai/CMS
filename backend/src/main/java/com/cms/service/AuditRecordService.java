package com.cms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cms.entity.AuditRecord;

import java.util.List;

public interface AuditRecordService extends IService<AuditRecord> {
    Page<AuditRecord> getAuditQueue(Integer pageNum, Integer pageSize, String title, Long categoryId);
    Page<AuditRecord> getAuditHistory(Integer pageNum, Integer pageSize, Long articleId, 
                                        Long submitterId, Long auditorId, Integer auditStatus);
    boolean createAuditRecord(AuditRecord auditRecord);
    boolean auditArticle(Long auditRecordId, Long auditorId, String auditorName, 
                         Integer auditStatus, String auditComment);
    boolean batchAudit(List<Long> auditRecordIds, Long auditorId, String auditorName,
                       Integer auditStatus, String auditComment);
}
