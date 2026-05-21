package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseDTO;
import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.dto.SalesDTO;
import com.sn.onepay.entity.Partnership;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.enumeration.StateStatus;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.PartnershipMapper;
import com.sn.onepay.repository.PartnershipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PartnershipServiceImplTest {

    @Mock
    PartnershipRepository partnershipRepository;

    @Mock
    PartnershipMapper partnershipMapper;

    @InjectMocks
    PartnershipServiceImpl partnershipService;

    private PartnershipDTO buildPartnershipDTO(Long salesId, Modules salesType, Long enterpriseId,
                                                List<Modules> enrolledModules, StateStatus status) {
        var salesDTO = mock(SalesDTO.class);
        when(salesDTO.id()).thenReturn(salesId);
        when(salesDTO.type()).thenReturn(salesType);

        var enterpriseDTO = mock(EnterpriseDTO.class);
        when(enterpriseDTO.id()).thenReturn(enterpriseId);
        when(enterpriseDTO.enrolledModules()).thenReturn(enrolledModules);

        var dto = mock(PartnershipDTO.class);
        when(dto.sales()).thenReturn(salesDTO);
        when(dto.enterprise()).thenReturn(enterpriseDTO);
        if (status != null) when(dto.status()).thenReturn(status);
        return dto;
    }

    @Test
    void createPartnership_throwsWhenAlreadyExists() {
        var dto = buildPartnershipDTO(1L, Modules.RESTAURATION, 2L, List.of(Modules.RESTAURATION), null);
        when(partnershipRepository.findPartnershipBySalesIdAndEnterpriseId(1L, 2L))
                .thenReturn(new Partnership());

        assertThatThrownBy(() -> partnershipService.createPartnership(dto))
                .isInstanceOf(ResourceAlreadyExistException.class);
    }

    @Test
    void createPartnership_throwsWhenModuleNotAllowed() {
        var dto = buildPartnershipDTO(1L, Modules.RESTAURATION, 2L, List.of(Modules.MARKET), null);
        when(partnershipRepository.findPartnershipBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(null);

        assertThatThrownBy(() -> partnershipService.createPartnership(dto))
                .isInstanceOf(ObjectValidationException.class);
    }

    @Test
    void createPartnership_happyPath() {
        var dto = buildPartnershipDTO(1L, Modules.RESTAURATION, 2L, List.of(Modules.RESTAURATION), StateStatus.ACTIVE);
        var entity = new Partnership();
        var saved = new Partnership();
        var resultDTO = mock(PartnershipDTO.class);

        when(partnershipRepository.findPartnershipBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(null);
        when(partnershipMapper.asEntity(dto)).thenReturn(entity);
        when(partnershipRepository.save(entity)).thenReturn(saved);
        when(partnershipMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = partnershipService.createPartnership(dto);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updatePartnership_throwsWhenNotFound() {
        when(partnershipRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnershipService.updatePartnership(mock(PartnershipDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updatePartnership_savesWhenFound() {
        var dto = mock(PartnershipDTO.class);
        var existing = new Partnership();
        var updated = new Partnership();
        var resultDTO = mock(PartnershipDTO.class);

        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(partnershipMapper.asEntity(dto)).thenReturn(updated);
        when(partnershipRepository.saveAndFlush(updated)).thenReturn(updated);
        when(partnershipMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = partnershipService.updatePartnership(dto, 1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void deletePartnership_throwsWhenNotFound() {
        when(partnershipRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnershipService.deletePartnership(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deletePartnership_deletesWhenFound() {
        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(new Partnership()));
        doNothing().when(partnershipRepository).deleteById(1L);

        partnershipService.deletePartnership(1L);

        verify(partnershipRepository).deleteById(1L);
    }

    @Test
    void getPartnershipsByFilters_withAllNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new Partnership()));
        when(partnershipRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(partnershipMapper.asDTO(any(Partnership.class))).thenReturn(mock(PartnershipDTO.class));

        Page<PartnershipDTO> result = partnershipService.getPartnershipsByFilters(
                null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getPartnershipsByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new Partnership()));
        when(partnershipRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(partnershipMapper.asDTO(any(Partnership.class))).thenReturn(mock(PartnershipDTO.class));

        Page<PartnershipDTO> result = partnershipService.getPartnershipsByFilters(
                1L, "REF", mock(com.sn.onepay.entity.Sales.class), mock(com.sn.onepay.entity.Enterprise.class),
                StateStatus.ACTIVE, LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getPartnershipsBySalesIdAndEnterpriseId_returnsMappedDTO() {
        var partnership = new Partnership();
        var resultDTO = mock(PartnershipDTO.class);

        when(partnershipRepository.findPartnershipBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(partnership);
        when(partnershipMapper.asDTO(partnership)).thenReturn(resultDTO);

        var result = partnershipService.getPartnershipsBySalesIdAndEnterpriseId(1L, 2L);
        assertThat(result).isEqualTo(resultDTO);
    }
}