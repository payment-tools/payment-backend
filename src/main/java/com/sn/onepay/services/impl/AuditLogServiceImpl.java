package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.AuditLogDTO;
import com.sn.onepay.entity.AuditLog;
import com.sn.onepay.entity.QAuditLog;
import com.sn.onepay.enumeration.AuditSeverity;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.AuditLogMapper;
import com.sn.onepay.repository.AuditLogRepository;
import com.sn.onepay.services.AuditLogService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    final AuditLogRepository auditLogRepository;
    final AuditLogMapper auditLogMapper;

    @Override
    public void recordEvent(String eventType, String httpMethod, String path, String actorUsername, String actorRole, String ipAddress, AuditSeverity severity, String details) {

        AuditLog auditLog = new AuditLog();
        auditLog.setEventType(eventType);
        auditLog.setHttpMethod(httpMethod);
        auditLog.setPath(path);
        auditLog.setActorUsername(actorUsername);
        auditLog.setActorRole(actorRole);
        auditLog.setIpAddress(ipAddress);
        auditLog.setSeverity(severity);
        auditLog.setDetails(details);
        auditLog.setActive(true);

        var savedAuditLog = auditLogRepository.save(auditLog);

        log.info("Recorded audit event: {}", savedAuditLog);
        log.trace("Recorded audit event with id: {}", savedAuditLog.getId());
    }

    @Override
    public Page<AuditLogDTO> getAuditLogsByFilters(Long id, String eventType, String actorUsername, AuditSeverity severity, String ipAddress, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QAuditLog auditLog = QAuditLog.auditLog;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(auditLog.id.eq(id));
        }
        if (eventType != null && !eventType.isEmpty()) {
            builder.and(auditLog.eventType.containsIgnoreCase(eventType));
        }
        if (actorUsername != null && !actorUsername.isEmpty()) {
            builder.and(auditLog.actorUsername.containsIgnoreCase(actorUsername));
        }
        if (severity != null) {
            builder.and(auditLog.severity.eq(severity));
        }
        if (ipAddress != null && !ipAddress.isEmpty()) {
            builder.and(auditLog.ipAddress.containsIgnoreCase(ipAddress));
        }
        if (active != null) {
            builder.and(auditLog.active.eq(active));
        }
        if (creationDate != null) {
            builder.and(auditLog.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(auditLog.modificationDate.loe(modificationDate));
        }

        Page<AuditLog> result = auditLogRepository.findAll(builder, pageable);

        return result.map(auditLogMapper::asDTO);
    }

    @Override
    public AuditLogDTO getAuditLogById(Long id) {
        return auditLogMapper.asDTO(auditLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AuditLog", "ID", id)));
    }
}
