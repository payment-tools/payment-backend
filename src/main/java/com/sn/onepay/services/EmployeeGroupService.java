package com.sn.onepay.services;

import com.sn.onepay.dto.EmployeeGroupDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EmployeeGroupService {

    EmployeeGroupDTO createEmployeeGroup(EmployeeGroupDTO employeeGroupDTO);

    EmployeeGroupDTO updateEmployeeGroup(EmployeeGroupDTO employeeGroupDTO, Long employeeGroupId);

    void deleteEmployeeGroup(Long employeeGroupId);

    Page<EmployeeGroupDTO> getEmployeeGroupsByFilters(Long id, String ref, String name, Long enterpriseId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);
}