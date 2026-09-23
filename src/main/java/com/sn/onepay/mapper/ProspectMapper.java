package com.sn.onepay.mapper;

import com.sn.onepay.dto.ProspectDTO;
import com.sn.onepay.entity.Prospect;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProspectMapper extends IMapper<Prospect, ProspectDTO> {
}
