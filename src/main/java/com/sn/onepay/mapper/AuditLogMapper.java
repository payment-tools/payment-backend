package com.sn.onepay.mapper;

import com.sn.onepay.dto.AuditLogDTO;
import com.sn.onepay.entity.AuditLog;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper extends IMapper<AuditLog, AuditLogDTO> {
}
