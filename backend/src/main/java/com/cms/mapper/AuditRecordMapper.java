package com.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.entity.AuditRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditRecordMapper extends BaseMapper<AuditRecord> {
}
