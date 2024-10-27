package com.sn.onepay.mapper;


import com.sn.onepay.dto.EnterpriseDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnterpriseMapper extends IMapper<Enterprise, EnterpriseDTO> {
}
