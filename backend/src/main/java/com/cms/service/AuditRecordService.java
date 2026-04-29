package com.cms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.entity.AuditRecord;
import com.cms.mapper.AuditRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditRecordService {

    private final AuditRecordMapper auditRecordMapper;

    public AuditRecordService(AuditRecordMapper auditRecordMapper) {
        this.auditRecordMapper = auditRecordMapper;
    }

    public Page<AuditRecord> getRecordPage(Integer pageNum, Integer pageSize, 
                                             Long articleId, Integer auditStatus) {
        Page<AuditRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AuditRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (articleId != null) {
            wrapper.eq(AuditRecord::getArticleId, articleId);
        }
        if (auditStatus != null) {
            wrapper.eq(AuditRecord::getAuditStatus, auditStatus);
        }
        
        wrapper.orderByDesc(AuditRecord::getCreateTime);
        return auditRecordMapper.selectPage(page, wrapper);
    }

    public List<AuditRecord> getRecordsByArticleId(Long articleId) {
        LambdaQueryWrapper<AuditRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuditRecord::getArticleId, articleId)
               .orderByDesc(AuditRecord::getCreateTime);
        return auditRecordMapper.selectList(wrapper);
    }

    public AuditRecord getById(Long id) {
        return auditRecordMapper.selectById(id);
    }

    public boolean save(AuditRecord auditRecord) {
        auditRecord.setCreateTime(LocalDateTime.now());
        auditRecord.setUpdateTime(LocalDateTime.now());
        return auditRecordMapper.insert(auditRecord) > 0;
    }

    public boolean update(AuditRecord auditRecord) {
        auditRecord.setUpdateTime(LocalDateTime.now());
        return auditRecordMapper.updateById(auditRecord) > 0;
    }

    public boolean delete(Long id) {
        return auditRecordMapper.deleteById(id) > 0;
    }

    @Transactional
    public AuditRecord createSubmitRecord(Long articleId, Long submitterId, String submitterName) {
        AuditRecord record = new AuditRecord();
        record.setArticleId(articleId);
        record.setSubmitterId(submitterId);
        record.setSubmitterName(submitterName);
        record.setSubmitTime(LocalDateTime.now());
        record.setAuditStatus(0);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        auditRecordMapper.insert(record);
        return record;
    }

    @Transactional
    public AuditRecord updateAuditResult(Long recordId, Long auditorId, String auditorName, 
                                          Integer auditStatus, String auditComment) {
        AuditRecord record = auditRecordMapper.selectById(recordId);
        if (record == null) {
            return null;
        }
        
        record.setAuditorId(auditorId);
        record.setAuditorName(auditorName);
        record.setAuditTime(LocalDateTime.now());
        record.setAuditStatus(auditStatus);
        record.setAuditComment(auditComment);
        record.setUpdateTime(LocalDateTime.now());
        
        auditRecordMapper.updateById(record);
        return record;
    }
}
