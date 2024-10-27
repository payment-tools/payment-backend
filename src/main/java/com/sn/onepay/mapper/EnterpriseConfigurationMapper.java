package com.sn.onepay.mapper;

import com.sn.onepay.dto.EnterpriseConfigurationDTO;
import com.sn.onepay.entity.EnterpriseConfiguration;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnterpriseConfigurationMapper extends IMapper<EnterpriseConfiguration, EnterpriseConfigurationDTO> {
}
