package com.sn.onepay.mapper;

import com.sn.onepay.dto.CashierDTO;
import com.sn.onepay.entity.Cashier;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CashierMapper extends IMapper<Cashier, CashierDTO> {
}
