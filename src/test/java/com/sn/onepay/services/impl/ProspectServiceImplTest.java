package com.sn.onepay.services.impl;

import com.sn.onepay.dto.ProspectCreateDTO;
import com.sn.onepay.dto.ProspectDTO;
import com.sn.onepay.dto.ProspectUpdateDTO;
import com.sn.onepay.entity.Prospect;
import com.sn.onepay.enumeration.ProspectScore;
import com.sn.onepay.enumeration.ProspectStage;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.ProspectMapper;
import com.sn.onepay.repository.ProspectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class ProspectServiceImplTest {

    @Mock
    ProspectRepository prospectRepository;

    @Mock
    ProspectMapper prospectMapper;

    @InjectMocks
    ProspectServiceImpl prospectService;

    @Test
    void createProspect_savesAndReturnsDTOWithExplicitStageAndScore() {
        var createDTO = mock(ProspectCreateDTO.class);
        when(createDTO.companyName()).thenReturn("Acme SARL");
        when(createDTO.contactName()).thenReturn("Awa Ndiaye");
        when(createDTO.email()).thenReturn("awa@acme.sn");
        when(createDTO.phoneNumber()).thenReturn("770000000");
        when(createDTO.stage()).thenReturn(ProspectStage.QUALIFICATION);
        when(createDTO.note()).thenReturn("Contact chaleureux");
        when(createDTO.potentialEmployees()).thenReturn(120);
        when(createDTO.assignedTo()).thenReturn("Moussa Diop");
        when(createDTO.score()).thenReturn(ProspectScore.HOT);

        var saved = new Prospect();
        var resultDTO = mock(ProspectDTO.class);

        when(prospectRepository.save(any(Prospect.class))).thenReturn(saved);
        when(prospectMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = prospectService.createProspect(createDTO);

        assertThat(result).isEqualTo(resultDTO);

        ArgumentCaptor<Prospect> captor = ArgumentCaptor.forClass(Prospect.class);
        verify(prospectRepository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isTrue();
        assertThat(captor.getValue().getRef()).isNotBlank();
        assertThat(captor.getValue().getCompanyName()).isEqualTo("Acme SARL");
        assertThat(captor.getValue().getContactName()).isEqualTo("Awa Ndiaye");
        assertThat(captor.getValue().getEmail()).isEqualTo("awa@acme.sn");
        assertThat(captor.getValue().getPhoneNumber()).isEqualTo("770000000");
        assertThat(captor.getValue().getStage()).isEqualTo(ProspectStage.QUALIFICATION);
        assertThat(captor.getValue().getNote()).isEqualTo("Contact chaleureux");
        assertThat(captor.getValue().getPotentialEmployees()).isEqualTo(120);
        assertThat(captor.getValue().getAssignedTo()).isEqualTo("Moussa Diop");
        assertThat(captor.getValue().getScore()).isEqualTo(ProspectScore.HOT);
    }

    @Test
    void createProspect_appliesDefaultStageAndScoreWhenNull() {
        var createDTO = mock(ProspectCreateDTO.class);
        when(createDTO.stage()).thenReturn(null);
        when(createDTO.score()).thenReturn(null);

        var saved = new Prospect();
        var resultDTO = mock(ProspectDTO.class);

        when(prospectRepository.save(any(Prospect.class))).thenReturn(saved);
        when(prospectMapper.asDTO(saved)).thenReturn(resultDTO);

        prospectService.createProspect(createDTO);

        ArgumentCaptor<Prospect> captor = ArgumentCaptor.forClass(Prospect.class);
        verify(prospectRepository).save(captor.capture());
        assertThat(captor.getValue().getStage()).isEqualTo(ProspectStage.DECOUVERTE);
        assertThat(captor.getValue().getScore()).isEqualTo(ProspectScore.COLD);
    }

    @Test
    void updateProspect_throwsWhenNotFound() {
        when(prospectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prospectService.updateProspect(mock(ProspectUpdateDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateProspect_updatesAllFieldsWhenProvided() {
        var dto = mock(ProspectUpdateDTO.class);
        when(dto.companyName()).thenReturn("Acme SARL");
        when(dto.contactName()).thenReturn("Awa Ndiaye");
        when(dto.email()).thenReturn("awa@acme.sn");
        when(dto.phoneNumber()).thenReturn("770000000");
        when(dto.stage()).thenReturn(ProspectStage.ONBOARDING);
        when(dto.note()).thenReturn("Signature prevue");
        when(dto.potentialEmployees()).thenReturn(200);
        when(dto.assignedTo()).thenReturn("Moussa Diop");
        when(dto.score()).thenReturn(ProspectScore.HOT);
        when(dto.active()).thenReturn(true);

        var existing = new Prospect();
        var saved = new Prospect();
        var resultDTO = mock(ProspectDTO.class);

        when(prospectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(prospectRepository.saveAndFlush(existing)).thenReturn(saved);
        when(prospectMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = prospectService.updateProspect(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getCompanyName()).isEqualTo("Acme SARL");
        assertThat(existing.getContactName()).isEqualTo("Awa Ndiaye");
        assertThat(existing.getEmail()).isEqualTo("awa@acme.sn");
        assertThat(existing.getPhoneNumber()).isEqualTo("770000000");
        assertThat(existing.getStage()).isEqualTo(ProspectStage.ONBOARDING);
        assertThat(existing.getNote()).isEqualTo("Signature prevue");
        assertThat(existing.getPotentialEmployees()).isEqualTo(200);
        assertThat(existing.getAssignedTo()).isEqualTo("Moussa Diop");
        assertThat(existing.getScore()).isEqualTo(ProspectScore.HOT);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void updateProspect_keepsExistingFieldsWhenDtoFieldsNull() {
        /*Mockito's default answer for an unstubbed boxed-type accessor (Integer/Boolean) is
          the zero value, not null, so these need to be stubbed explicitly to exercise the
          "field not provided" branch*/
        var dto = mock(ProspectUpdateDTO.class);
        when(dto.potentialEmployees()).thenReturn(null);
        when(dto.active()).thenReturn(null);

        var existing = new Prospect();
        existing.setCompanyName("Original SARL");
        existing.setPotentialEmployees(50);
        existing.setActive(true);
        var saved = new Prospect();
        var resultDTO = mock(ProspectDTO.class);

        when(prospectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(prospectRepository.saveAndFlush(existing)).thenReturn(saved);
        when(prospectMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = prospectService.updateProspect(dto, 1L);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(existing.getCompanyName()).isEqualTo("Original SARL");
        assertThat(existing.getPotentialEmployees()).isEqualTo(50);
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deleteProspect_throwsWhenNotFound() {
        when(prospectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prospectService.deleteProspect(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteProspect_setsActiveToFalse() {
        var prospect = new Prospect();
        when(prospectRepository.findById(1L)).thenReturn(Optional.of(prospect));
        when(prospectRepository.saveAndFlush(prospect)).thenReturn(prospect);

        prospectService.deleteProspect(1L);

        assertThat(prospect.isActive()).isFalse();
        verify(prospectRepository).saveAndFlush(prospect);
    }

    @Test
    void getProspectsByFilters_withAllNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new Prospect()));
        when(prospectRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(prospectMapper.asDTO(any(Prospect.class))).thenReturn(mock(ProspectDTO.class));

        Page<ProspectDTO> result = prospectService.getProspectsByFilters(
                null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getProspectsByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new Prospect()));
        when(prospectRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(prospectMapper.asDTO(any(Prospect.class))).thenReturn(mock(ProspectDTO.class));

        Page<ProspectDTO> result = prospectService.getProspectsByFilters(
                1L, "REF", "Acme", ProspectStage.QUALIFICATION, ProspectScore.HOT, "Moussa",
                true, LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getProspectsByFilters_withEmptyStringParams_returnsPage() {
        var page = new PageImpl<>(List.of(new Prospect()));
        when(prospectRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(prospectMapper.asDTO(any(Prospect.class))).thenReturn(mock(ProspectDTO.class));

        Page<ProspectDTO> result = prospectService.getProspectsByFilters(
                null, "", "", null, null, "", null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getProspectById_returnsMappedDTOWhenFound() {
        var entity = new Prospect();
        var resultDTO = mock(ProspectDTO.class);

        when(prospectRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(prospectMapper.asDTO(entity)).thenReturn(resultDTO);

        var result = prospectService.getProspectById(1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void getProspectById_throwsWhenNotFound() {
        when(prospectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prospectService.getProspectById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
