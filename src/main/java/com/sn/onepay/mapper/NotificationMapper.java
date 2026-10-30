package com.sn.onepay.mapper;

import com.sn.onepay.dto.NotificationDTO;
import com.sn.onepay.entity.Notification;
import com.sn.onepay.utils.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper extends IMapper<Notification, NotificationDTO> {
}
