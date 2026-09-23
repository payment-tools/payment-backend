package com.sn.onepay.services;

import com.sn.onepay.dto.ProspectCreateDTO;
import com.sn.onepay.dto.ProspectDTO;
import com.sn.onepay.dto.ProspectUpdateDTO;
import com.sn.onepay.enumeration.ProspectScore;
import com.sn.onepay.enumeration.ProspectStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ProspectService {

    ProspectDTO createProspect(ProspectCreateDTO prospectCreateDTO);

    ProspectDTO updateProspect(ProspectUpdateDTO prospectUpdateDTO, Long prospectId);

    void deleteProspect(Long prospectId);

    Page<ProspectDTO> getProspectsByFilters(Long id, String ref, String companyName, ProspectStage stage, ProspectScore score, String assignedTo, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);

    ProspectDTO getProspectById(Long id);
}
