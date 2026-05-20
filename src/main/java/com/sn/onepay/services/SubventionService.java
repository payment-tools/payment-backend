package com.sn.onepay.services;

import com.sn.onepay.dto.SubventionDTO;
import com.sn.onepay.enumeration.StateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SubventionService {

    SubventionDTO createSubvention(SubventionDTO subventionDTO);

    SubventionDTO updateSubvention(SubventionDTO subventionDTO, Long subventionId);

    void deleteSubvention(Long subventionId);

    Page<SubventionDTO> getSubventionsByFilters(Long id, String ref, Double employeePercent, Double employerPercent, Long partnershipId, Long employeeGroupId, StateStatus status, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);
}