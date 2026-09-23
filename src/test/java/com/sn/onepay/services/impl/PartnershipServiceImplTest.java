package com.sn.onepay.services.impl;

import com.sn.onepay.dto.PartnershipCreateDTO;
import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.dto.PartnershipUpdateDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.Partnership;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.PartnershipMapper;
import com.sn.onepay.repository.EnterpriseRepository;
import com.sn.onepay.repository.PartnershipRepository;
import com.sn.onepay.repository.SalesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @Mock
    EnterpriseRepository enterpriseRepository;

    @Mock
    SalesRepository salesRepository;

    @InjectMocks
    PartnershipServiceImpl partnershipService;

    private PartnershipCreateDTO buildPartnershipCreateDTO(Long salesId, Long enterpriseId) {
        var dto = mock(PartnershipCreateDTO.class);
        when(dto.salesId()).thenReturn(salesId);
        when(dto.enterpriseId()).thenReturn(enterpriseId);
        return dto;
    }

    private PartnershipUpdateDTO buildPartnershipUpdateDTO(Long salesId, Long enterpriseId) {
        var dto = mock(PartnershipUpdateDTO.class);
        when(dto.salesId()).thenReturn(salesId);
        when(dto.enterpriseId()).thenReturn(enterpriseId);
        return dto;
    }

    private Enterprise buildEnterprise(Long id, List<Modules> enrolledModules) {
        var enterprise = new Enterprise();
        enterprise.setId(id);
        enterprise.setEnrolledModules(enrolledModules);
        return enterprise;
    }

    private Sales buildSales(Long id, Modules type) {
        var sales = new Sales();
        sales.setId(id);
        sales.setType(type);
        return sales;
    }

    @Test
    void createPartnership_throwsWhenEnterpriseNotFound() {
        var dto = buildPartnershipCreateDTO(1L, 99L);
        when(enterpriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnershipService.createPartnership(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createPartnership_throwsWhenSalesNotFound() {
        var dto = buildPartnershipCreateDTO(99L, 2L);
        when(enterpriseRepository.findById(2L)).thenReturn(Optional.of(buildEnterprise(2L, List.of(Modules.RESTAURATION))));
        when(salesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnershipService.createPartnership(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createPartnership_throwsWhenAlreadyExists() {
        var dto = buildPartnershipCreateDTO(1L, 2L);
        when(salesRepository.findById(1L)).thenReturn(Optional.of(buildSales(1L, Modules.RESTAURATION)));
        when(enterpriseRepository.findById(2L)).thenReturn(Optional.of(buildEnterprise(2L, List.of(Modules.RESTAURATION))));
        when(partnershipRepository.findPartnershipBySalesIdAndEnterpriseId(1L, 2L))
                .thenReturn(new Partnership());

        assertThatThrownBy(() -> partnershipService.createPartnership(dto))
                .isInstanceOf(ResourceAlreadyExistException.class);
    }

    @Test
    void createPartnership_throwsWhenModuleNotAllowed() {
        var dto = buildPartnershipCreateDTO(1L, 2L);
        when(salesRepository.findById(1L)).thenReturn(Optional.of(buildSales(1L, Modules.RESTAURATION)));
        when(enterpriseRepository.findById(2L)).thenReturn(Optional.of(buildEnterprise(2L, List.of(Modules.MARKET))));
        when(partnershipRepository.findPartnershipBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(null);

        assertThatThrownBy(() -> partnershipService.createPartnership(dto))
                .isInstanceOf(ObjectValidationException.class);
    }

    @Test
    void createPartnership_throwsWhenEnterpriseHasNoEnrolledModules() {
        var dto = buildPartnershipCreateDTO(1L, 2L);
        when(salesRepository.findById(1L)).thenReturn(Optional.of(buildSales(1L, Modules.RESTAURATION)));
        when(enterpriseRepository.findById(2L)).thenReturn(Optional.of(buildEnterprise(2L, null)));
        when(partnershipRepository.findPartnershipBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(null);

        assertThatThrownBy(() -> partnershipService.createPartnership(dto))
                .isInstanceOf(ObjectValidationException.class);
    }

    @Test
    void createPartnership_happyPath() {
        var dto = buildPartnershipCreateDTO(1L, 2L);
        var sales = buildSales(1L, Modules.RESTAURATION);
        var enterprise = buildEnterprise(2L, List.of(Modules.RESTAURATION));
        var saved = new Partnership();
        var resultDTO = mock(PartnershipDTO.class);

        when(salesRepository.findById(1L)).thenReturn(Optional.of(sales));
        when(enterpriseRepository.findById(2L)).thenReturn(Optional.of(enterprise));
        when(partnershipRepository.findPartnershipBySalesIdAndEnterpriseId(1L, 2L)).thenReturn(null);
        when(partnershipRepository.save(any(Partnership.class))).thenReturn(saved);
        when(partnershipMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = partnershipService.createPartnership(dto);

        assertThat(result).isEqualTo(resultDTO);

        ArgumentCaptor<Partnership> captor = ArgumentCaptor.forClass(Partnership.class);
        verify(partnershipRepository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isTrue();
        assertThat(captor.getValue().getRef()).isNotBlank();
        assertThat(captor.getValue().getSales()).isEqualTo(sales);
        assertThat(captor.getValue().getEnterprise()).isEqualTo(enterprise);
    }

    @Test
    void updatePartnership_throwsWhenNotFound() {
        when(partnershipRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnershipService.updatePartnership(mock(PartnershipUpdateDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updatePartnership_updatesAllFieldsWhenProvided() {
        var dto = buildPartnershipUpdateDTO(1L, 2L);
        when(dto.active()).thenReturn(true);

        var existing = new Partnership();
        var sales = buildSales(1L, Modules.RESTAURATION);
        var enterprise = buildEnterprise(2L, List.of(Modules.RESTAURATION));
        var saved = new Partnership();
        var resultDTO = mock(PartnershipDTO.class);

        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(salesRepository.findById(1L)).thenReturn(Optional.of(sales));
        when(enterpriseRepository.findById(2L)).thenReturn(Optional.of(enterprise));
        when(partnershipRepository.saveAndFlush(existing)).thenReturn(saved);
        when(partnershipMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = partnershipService.updatePartnership(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getSales()).isEqualTo(sales);
        assertThat(existing.getEnterprise()).isEqualTo(enterprise);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void updatePartnership_throwsWhenSalesNotFound() {
        var dto = buildPartnershipUpdateDTO(99L, 2L);
        var existing = new Partnership();

        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(salesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnershipService.updatePartnership(dto, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updatePartnership_throwsWhenEnterpriseNotFound() {
        var dto = buildPartnershipUpdateDTO(1L, 99L);
        var existing = new Partnership();

        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(salesRepository.findById(1L)).thenReturn(Optional.of(buildSales(1L, Modules.RESTAURATION)));
        when(enterpriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnershipService.updatePartnership(dto, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updatePartnership_keepsExistingFieldsWhenDtoFieldsNull() {
        /*Mockito's default answer for an unstubbed boxed-type accessor (Long/Boolean) is
          the zero-equivalent, not null, so these need to be stubbed explicitly to exercise the
          "field not provided" branch*/
        var dto = mock(PartnershipUpdateDTO.class);
        when(dto.salesId()).thenReturn(null);
        when(dto.enterpriseId()).thenReturn(null);
        when(dto.active()).thenReturn(null);

        var existing = new Partnership();
        existing.setRef("Original");
        existing.setActive(true);
        var saved = new Partnership();
        var resultDTO = mock(PartnershipDTO.class);

        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(partnershipRepository.saveAndFlush(existing)).thenReturn(saved);
        when(partnershipMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = partnershipService.updatePartnership(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getRef()).isEqualTo("Original");
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deletePartnership_throwsWhenNotFound() {
        when(partnershipRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnershipService.deletePartnership(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deletePartnership_setsActiveToFalse() {
        var partnership = new Partnership();
        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(partnership));
        when(partnershipRepository.saveAndFlush(partnership)).thenReturn(partnership);

        partnershipService.deletePartnership(1L);

        assertThat(partnership.isActive()).isFalse();
        verify(partnershipRepository).saveAndFlush(partnership);
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
                1L, "REF", 1L, 2L, true,
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

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

    @Test
    void getPartnershipById_returnsMappedDTOWhenFound() {
        var entity = new Partnership();
        var resultDTO = mock(PartnershipDTO.class);

        when(partnershipRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(partnershipMapper.asDTO(entity)).thenReturn(resultDTO);

        var result = partnershipService.getPartnershipById(1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void getPartnershipById_throwsWhenNotFound() {
        when(partnershipRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partnershipService.getPartnershipById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
