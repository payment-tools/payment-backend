package com.sn.onepay.services;

import com.sn.onepay.dto.AuditLogDTO;
import com.sn.onepay.enumeration.AuditSeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuditLogService {

    void recordEvent(String eventType, String httpMethod, String path, String actorUsername, String actorRole, String ipAddress, AuditSeverity severity, String details);

    Page<AuditLogDTO> getAuditLogsByFilters(Long id, String eventType, String actorUsername, AuditSeverity severity, String ipAddress, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);

    AuditLogDTO getAuditLogById(Long id);
}
