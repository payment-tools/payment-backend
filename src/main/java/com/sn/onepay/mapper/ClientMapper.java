package com.sn.onepay.mapper;

import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.entity.Client;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper extends IMapper<Client, ClientDTO> {
}
