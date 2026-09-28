package com.sn.onepay.services;

import com.sn.onepay.dto.PlatformUserDTO;
import com.sn.onepay.enumeration.PlatformUserType;
import com.sn.onepay.enumeration.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlatformUserService {

    Page<PlatformUserDTO> getPlatformUsersByFilters(String search, Roles role, PlatformUserType userType, Boolean active, Pageable pageable);

    PlatformUserDTO getPlatformUserByTypeAndId(PlatformUserType userType, Long id);
}
