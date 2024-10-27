package com.sn.onepay.mapper;

import com.sn.onepay.dto.EnterpriseProfileDTO;
import com.sn.onepay.entity.EnterpriseProfile;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnterpriseProfileMapper extends IMapper<EnterpriseProfile, EnterpriseProfileDTO> {
}
