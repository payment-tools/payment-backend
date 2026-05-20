package com.sn.onepay.mapper;

import com.sn.onepay.dto.EmployeeGroupDTO;
import com.sn.onepay.entity.EmployeeGroup;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {EnterpriseMapper.class, ClientMapper.class})
public interface EmployeeGroupMapper extends IMapper<EmployeeGroup, EmployeeGroupDTO> {
}