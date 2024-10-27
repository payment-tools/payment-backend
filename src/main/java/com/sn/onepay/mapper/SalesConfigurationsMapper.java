package com.sn.onepay.mapper;

import com.sn.onepay.dto.SalesConfigurationsDTO;
import com.sn.onepay.entity.SalesConfigurations;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SalesConfigurationsMapper extends IMapper<SalesConfigurations, SalesConfigurationsDTO> {
}
