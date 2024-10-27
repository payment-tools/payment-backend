package com.sn.onepay.mapper;

import com.sn.onepay.dto.BillsDTO;
import com.sn.onepay.entity.Bills;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BillsMapper extends IMapper<Bills, BillsDTO> {
}
