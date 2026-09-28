package com.sn.onepay.services.impl;

import com.sn.onepay.dto.AuditLogDTO;
import com.sn.onepay.entity.AuditLog;
import com.sn.onepay.enumeration.AuditSeverity;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.AuditLogMapper;
import com.sn.onepay.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    AuditLogRepository auditLogRepository;

    @Mock
    AuditLogMapper auditLogMapper;

    @InjectMocks
    AuditLogServiceImpl auditLogService;

    @Test
    void recordEvent_savesAuditLog() {
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        auditLogService.recordEvent("Creation Client", "POST", "/v1/onepay/client", "jdoe", "SUPER_ADMIN", "127.0.0.1", AuditSeverity.INFO, "detail");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertThat(captor.getValue().getEventType()).isEqualTo("Creation Client");
        assertThat(captor.getValue().getHttpMethod()).isEqualTo("POST");
        assertThat(captor.getValue().getPath()).isEqualTo("/v1/onepay/client");
        assertThat(captor.getValue().getActorUsername()).isEqualTo("jdoe");
        assertThat(captor.getValue().getActorRole()).isEqualTo("SUPER_ADMIN");
        assertThat(captor.getValue().getIpAddress()).isEqualTo("127.0.0.1");
        assertThat(captor.getValue().getSeverity()).isEqualTo(AuditSeverity.INFO);
        assertThat(captor.getValue().getDetails()).isEqualTo("detail");
        assertThat(captor.getValue().isActive()).isTrue();
    }

    @Test
    void getAuditLogsByFilters_withAllNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new AuditLog()));
        when(auditLogRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(auditLogMapper.asDTO(any(AuditLog.class))).thenReturn(mock(AuditLogDTO.class));

        Page<AuditLogDTO> result = auditLogService.getAuditLogsByFilters(
                null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getAuditLogsByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new AuditLog()));
        when(auditLogRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(auditLogMapper.asDTO(any(AuditLog.class))).thenReturn(mock(AuditLogDTO.class));

        Page<AuditLogDTO> result = auditLogService.getAuditLogsByFilters(
                1L, "Creation", "jdoe", AuditSeverity.INFO, "127.0.0.1", true,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getAuditLogsByFilters_withEmptyStringParams_returnsPage() {
        var page = new PageImpl<>(List.of(new AuditLog()));
        when(auditLogRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(auditLogMapper.asDTO(any(AuditLog.class))).thenReturn(mock(AuditLogDTO.class));

        Page<AuditLogDTO> result = auditLogService.getAuditLogsByFilters(
                null, "", "", null, "", null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getAuditLogById_returnsMappedDTOWhenFound() {
        var entity = new AuditLog();
        var resultDTO = mock(AuditLogDTO.class);

        when(auditLogRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(auditLogMapper.asDTO(entity)).thenReturn(resultDTO);

        var result = auditLogService.getAuditLogById(1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void getAuditLogById_throwsWhenNotFound() {
        when(auditLogRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auditLogService.getAuditLogById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
