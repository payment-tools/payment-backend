package com.sn.onepay.services;

import com.sn.onepay.dto.SubventionCreateDTO;
import com.sn.onepay.dto.SubventionDTO;
import com.sn.onepay.dto.SubventionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SubventionService {

    SubventionDTO createSubvention(SubventionCreateDTO subventionCreateDTO);

    SubventionDTO updateSubvention(SubventionUpdateDTO subventionUpdateDTO, Long subventionId);

    void deleteSubvention(Long subventionId);

    Page<SubventionDTO> getSubventionsByFilters(Long id, String ref, Double employeePercent, Double employerPercent, Long partnershipId, Long employeeGroupId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);
}
