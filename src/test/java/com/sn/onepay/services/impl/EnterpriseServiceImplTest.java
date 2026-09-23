package com.sn.onepay.services.impl;

import com.sn.onepay.dto.EnterpriseDTO;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.EnterpriseMapper;
import com.sn.onepay.repository.EnterpriseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class EnterpriseServiceImplTest {

    @Mock
    EnterpriseRepository enterpriseRepository;

    @Mock
    EnterpriseMapper enterpriseMapper;

    @InjectMocks
    EnterpriseServiceImpl enterpriseService;

    @Test
    void createEnterprise_setsActualQuotaToZeroAndSaves() {
        var dto = mock(EnterpriseDTO.class);
        var entity = new Enterprise();
        var saved = new Enterprise();
        var resultDTO = mock(EnterpriseDTO.class);

        when(enterpriseMapper.asEntity(dto)).thenReturn(entity);
        when(enterpriseRepository.save(entity)).thenReturn(saved);
        when(enterpriseMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = enterpriseService.createEnterprise(dto);

        assertThat(entity.getActualQuota()).isEqualTo(0L);
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getRef()).isNotBlank();
        assertThat(result).isEqualTo(resultDTO);
        verify(enterpriseRepository).save(entity);
    }

    @Test
    void updateEnterprise_throwsWhenNotFound() {
        when(enterpriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseService.updateEnterprise(mock(EnterpriseDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateEnterprise_updatesAllFieldsWhenProvided() {
        var dto = mock(EnterpriseDTO.class);
        when(dto.ref()).thenReturn("REF1");
        when(dto.name()).thenReturn("Acme");
        when(dto.maxQuota()).thenReturn(100L);
        when(dto.actualQuota()).thenReturn(50L);
        when(dto.address()).thenReturn("Dakar");
        when(dto.enrolledModules()).thenReturn(List.of(Modules.RESTAURATION));
        when(dto.active()).thenReturn(true);

        var existing = new Enterprise();
        var saved = new Enterprise();
        var resultDTO = mock(EnterpriseDTO.class);

        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enterpriseRepository.saveAndFlush(existing)).thenReturn(saved);
        when(enterpriseMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = enterpriseService.updateEnterprise(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getRef()).isEqualTo("REF1");
        assertThat(existing.getName()).isEqualTo("Acme");
        assertThat(existing.getMaxQuota()).isEqualTo(100L);
        assertThat(existing.getActualQuota()).isEqualTo(50L);
        assertThat(existing.getAddress()).isEqualTo("Dakar");
        assertThat(existing.getEnrolledModules()).containsExactly(Modules.RESTAURATION);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void updateEnterprise_keepsExistingFieldsWhenDtoFieldsNull() {
        /*Mockito's default answer for an unstubbed boxed-type accessor (Long/Boolean) is
          the zero value, not null, so these need to be stubbed explicitly to exercise the
          "field not provided" branch*/
        var dto = mock(EnterpriseDTO.class);
        when(dto.maxQuota()).thenReturn(null);
        when(dto.actualQuota()).thenReturn(null);
        when(dto.enrolledModules()).thenReturn(null);
        when(dto.active()).thenReturn(null);

        var existing = new Enterprise();
        existing.setName("Original");
        existing.setMaxQuota(100L);
        existing.setActive(true);
        var saved = new Enterprise();
        var resultDTO = mock(EnterpriseDTO.class);

        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enterpriseRepository.saveAndFlush(existing)).thenReturn(saved);
        when(enterpriseMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = enterpriseService.updateEnterprise(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getName()).isEqualTo("Original");
        assertThat(existing.getMaxQuota()).isEqualTo(100L);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deleteEnterprise_throwsWhenNotFound() {
        when(enterpriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseService.deleteEnterprise(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteEnterprise_deletesWhenFound() {
        var existing = new Enterprise();
        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enterpriseRepository.saveAndFlush(existing)).thenReturn(existing);

        enterpriseService.deleteEnterprise(1L);

        assertThat(existing.isActive()).isFalse();
        verify(enterpriseRepository).saveAndFlush(existing);
    }

    @Test
    void getEnterprisesByFilters_withAllNullParams_returnsPage() {
        var entityPage = new PageImpl<>(List.of(new Enterprise()));
        when(enterpriseRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(entityPage);
        when(enterpriseMapper.asDTO(any(Enterprise.class))).thenReturn(mock(EnterpriseDTO.class));

        Page<EnterpriseDTO> result = enterpriseService.getEnterprisesByFilters(
                null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getEnterprisesByFilters_withAllParamsSet_returnsPage() {
        var entityPage = new PageImpl<>(List.of(new Enterprise()));
        when(enterpriseRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(entityPage);
        when(enterpriseMapper.asDTO(any(Enterprise.class))).thenReturn(mock(EnterpriseDTO.class));

        Page<EnterpriseDTO> result = enterpriseService.getEnterprisesByFilters(
                1L, "REF01", "MyEnterprise", 100L, 50L, "Dakar",
                Modules.RESTAURATION, true, LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getEnterpriseById_returnsMappedDTOWhenFound() {
        var entity = new Enterprise();
        var resultDTO = mock(EnterpriseDTO.class);

        when(enterpriseRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(enterpriseMapper.asDTO(entity)).thenReturn(resultDTO);

        var result = enterpriseService.getEnterpriseById(1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void getEnterpriseById_throwsWhenNotFound() {
        when(enterpriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enterpriseService.getEnterpriseById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}