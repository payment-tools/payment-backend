package com.sn.onepay.mapper;

import com.sn.onepay.dto.SalesDTO;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SalesMapper extends IMapper<Sales, SalesDTO> {
}
