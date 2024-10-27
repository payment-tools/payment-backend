package com.sn.onepay.mapper;

import com.sn.onepay.dto.SalesProfileDTO;
import com.sn.onepay.entity.SalesProfile;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SalesProfileMapper extends IMapper<SalesProfile, SalesProfileDTO> {
}
