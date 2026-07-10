package com.sn.onepay.mapper;

import com.sn.onepay.dto.BillsDTO;
import com.sn.onepay.entity.Bills;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PartnershipMapper.class})
public interface BillsMapper extends IMapper<Bills, BillsDTO> {
}
