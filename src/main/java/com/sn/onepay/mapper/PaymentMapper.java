package com.sn.onepay.mapper;

import com.sn.onepay.dto.PaymentDTO;
import com.sn.onepay.entity.Payment;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper extends IMapper<Payment, PaymentDTO> {
}
