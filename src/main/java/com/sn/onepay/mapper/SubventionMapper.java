package com.sn.onepay.mapper;

import com.sn.onepay.dto.SubventionDTO;
import com.sn.onepay.entity.Subvention;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PartnershipMapper.class, EmployeeGroupMapper.class})
public interface SubventionMapper extends IMapper<Subvention, SubventionDTO> {
}