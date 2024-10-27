package com.sn.onepay.mapper;

import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.entity.Partnership;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PartnershipMapper extends IMapper<Partnership, PartnershipDTO> {
}
