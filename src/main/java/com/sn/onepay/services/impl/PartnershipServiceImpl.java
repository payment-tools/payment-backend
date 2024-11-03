package com.sn.onepay.services.impl;

import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.services.PartnershipService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class PartnershipServiceImpl implements PartnershipService {
    @Override
    public PartnershipDTO createPartnership(PartnershipDTO partnershipDTO) {
        return null;
    }

    @Override
    public PartnershipDTO updatePartnership(PartnershipDTO partnershipDTO, Long partnershipId) {
        return null;
    }

    @Override
    public void deletePartnership(Long id) {

    }

    @Override
    public Page<PartnershipDTO> getPartnershipsByFilter(PartnershipDTO partnershipDTO, Pageable pageable) {
        return null;
    }
}
