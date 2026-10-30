package com.sn.onepay.services.impl;

import com.sn.onepay.dto.BillDetailsDTO;
import com.sn.onepay.dto.BillsDTO;
import com.sn.onepay.dto.PartnershipDTO;
import com.sn.onepay.entity.Bills;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.Partnership;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.BillStatus;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.BillsMapper;
import com.sn.onepay.repository.BillsRepository;
import com.sn.onepay.repository.PartnershipRepository;
import com.sn.onepay.repository.PaymentRepository;
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
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillsServiceImplTest {

    @Mock
    BillsRepository billsRepository;

    @Mock
    BillsMapper billsMapper;

    @Mock
    PartnershipRepository partnershipRepository;

    @Mock
    PaymentRepository paymentRepository;

    @InjectMocks
    BillsServiceImpl billsService;

    @Test
    void createBills_happyPath() {
        var dto = mock(BillsDTO.class);
        var entity = new Bills();
        var saved = new Bills();
        var resultDTO = mock(BillsDTO.class);

        when(billsMapper.asEntity(dto)).thenReturn(entity);
        when(billsRepository.save(any(Bills.class))).thenReturn(saved);
        when(billsMapper.asDTO(saved)).thenReturn(resultDTO);

        var result = billsService.createBills(dto);

        assertThat(result).isEqualTo(resultDTO);
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getRef()).isNotNull();
    }

    @Test
    void updateBills_throwsWhenNotFound() {
        when(billsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> billsService.updateBills(mock(BillsDTO.class), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateBills_modifiesFieldsAndSaves() {
        var startDate = LocalDateTime.of(2026, 7, 1, 0, 0);
        var endDate = LocalDateTime.of(2026, 7, 31, 23, 59);

        var dto = mock(BillsDTO.class);
        when(dto.startDate()).thenReturn(startDate);
        when(dto.endDate()).thenReturn(endDate);
        when(dto.totalAmount()).thenReturn(5000.0);
        when(dto.billStatus()).thenReturn(BillStatus.PAYED);
        when(dto.period()).thenReturn("2026-07");
        when(dto.active()).thenReturn(false);

        var existing = new Bills();
        existing.setActive(true);
        var updated = new Bills();
        var resultDTO = mock(BillsDTO.class);

        when(billsRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(billsRepository.saveAndFlush(existing)).thenReturn(updated);
        when(billsMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = billsService.updateBills(dto, 1L);

        assertThat(existing.getStartDate()).isEqualTo(startDate);
        assertThat(existing.getEndDate()).isEqualTo(endDate);
        assertThat(existing.getTotalAmount()).isEqualTo(5000.0);
        assertThat(existing.getBillStatus()).isEqualTo(BillStatus.PAYED);
        assertThat(existing.getPeriod()).isEqualTo("2026-07");
        assertThat(existing.isActive()).isFalse();
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateBills_validatesPaidBill() {
        var dto = mock(BillsDTO.class);
        when(dto.billStatus()).thenReturn(BillStatus.VALIDATED);

        var existing = new Bills();
        existing.setBillStatus(BillStatus.PAYED);
        var updated = new Bills();
        var resultDTO = mock(BillsDTO.class);

        when(billsRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(billsRepository.saveAndFlush(existing)).thenReturn(updated);
        when(billsMapper.asDTO(updated)).thenReturn(resultDTO);

        var result = billsService.updateBills(dto, 1L);

        assertThat(existing.getBillStatus()).isEqualTo(BillStatus.VALIDATED);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void updateBills_throwsWhenValidatingUnpaidBill() {
        var dto = mock(BillsDTO.class);
        when(dto.billStatus()).thenReturn(BillStatus.VALIDATED);

        var existing = new Bills();
        existing.setBillStatus(BillStatus.UNPAYED);

        when(billsRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> billsService.updateBills(dto, 1L))
                .isInstanceOf(ObjectValidationException.class);

        assertThat(existing.getBillStatus()).isEqualTo(BillStatus.UNPAYED);
        verify(billsRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateBills_withNullFields_doesNotChangeFields() {
        var startDate = LocalDateTime.of(2026, 7, 1, 0, 0);

        var dto = mock(BillsDTO.class);
        when(dto.startDate()).thenReturn(null);
        when(dto.endDate()).thenReturn(null);
        when(dto.totalAmount()).thenReturn(null);
        when(dto.billStatus()).thenReturn(null);
        when(dto.period()).thenReturn(null);
        when(dto.active()).thenReturn(null);

        var existing = new Bills();
        existing.setStartDate(startDate);
        existing.setTotalAmount(3000.0);
        existing.setBillStatus(BillStatus.UNPAYED);
        existing.setPeriod("2026-06");
        existing.setActive(true);

        var updated = new Bills();
        var resultDTO = mock(BillsDTO.class);

        when(billsRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(billsRepository.saveAndFlush(existing)).thenReturn(updated);
        when(billsMapper.asDTO(updated)).thenReturn(resultDTO);

        billsService.updateBills(dto, 1L);

        assertThat(existing.getStartDate()).isEqualTo(startDate);
        assertThat(existing.getTotalAmount()).isEqualTo(3000.0);
        assertThat(existing.getBillStatus()).isEqualTo(BillStatus.UNPAYED);
        assertThat(existing.getPeriod()).isEqualTo("2026-06");
        assertThat(existing.isActive()).isTrue();
    }

    @Test
    void deleteBills_throwsWhenNotFound() {
        when(billsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> billsService.deleteBills(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteBills_setsActiveToFalse() {
        var bills = new Bills();
        bills.setActive(true);
        when(billsRepository.findById(1L)).thenReturn(Optional.of(bills));
        when(billsRepository.saveAndFlush(bills)).thenReturn(bills);

        billsService.deleteBills(1L);

        assertThat(bills.isActive()).isFalse();
        verify(billsRepository).saveAndFlush(bills);
    }

    @Test
    void getBillsByFilters_withNullParams_returnsPage() {
        var page = new PageImpl<>(List.of(new Bills()));
        when(billsRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(billsMapper.asDTO(any(Bills.class))).thenReturn(mock(BillsDTO.class));

        Page<BillsDTO> result = billsService.getBillsByFilters(
                null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getBillsByFilters_withAllParamsSet_returnsPage() {
        var page = new PageImpl<>(List.of(new Bills()));
        when(billsRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(billsMapper.asDTO(any(Bills.class))).thenReturn(mock(BillsDTO.class));

        Page<BillsDTO> result = billsService.getBillsByFilters(
                1L, "REF", 2L, BillStatus.UNPAYED, "2026-07", true,
                LocalDateTime.now().minusMonths(1), LocalDateTime.now(),
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getBillsByFilters_withEmptyRefAndPeriod_returnsPage() {
        var page = new PageImpl<>(List.of(new Bills()));
        when(billsRepository.findAll(any(com.querydsl.core.types.Predicate.class), any(Pageable.class)))
                .thenReturn(page);
        when(billsMapper.asDTO(any(Bills.class))).thenReturn(mock(BillsDTO.class));

        Page<BillsDTO> result = billsService.getBillsByFilters(
                null, "", null, null, "", null, null, null, null, null, Pageable.unpaged());

        assertThat(result).hasSize(1);
    }

    @Test
    void getBillsById_returnsMappedDTOWhenFound() {
        var entity = new Bills();
        var resultDTO = mock(BillsDTO.class);

        when(billsRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(billsMapper.asDTO(entity)).thenReturn(resultDTO);

        var result = billsService.getBillsById(1L);
        assertThat(result).isEqualTo(resultDTO);
    }

    @Test
    void getBillsById_throwsWhenNotFound() {
        when(billsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> billsService.getBillsById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createBills_rejectsSecondBillForSamePartnershipAndPeriod() {
        var partnership = mock(PartnershipDTO.class);
        when(partnership.id()).thenReturn(1L);
        var dto = mock(BillsDTO.class);
        when(dto.partnership()).thenReturn(partnership);
        when(dto.period()).thenReturn("2026-10");
        when(billsRepository.existsByPartnership_IdAndPeriodAndActiveTrue(1L, "2026-10")).thenReturn(true);

        assertThatThrownBy(() -> billsService.createBills(dto))
                .isInstanceOf(ResourceAlreadyExistException.class)
                .hasMessage("Une facture existe déjà pour ce partenariat et cette période : 2026-10");
        verify(billsRepository, never()).save(any());
    }

    @Test
    void createBills_acceptsFirstBillForPartnershipAndPeriod() {
        var partnership = mock(PartnershipDTO.class);
        when(partnership.id()).thenReturn(1L);
        var dto = mock(BillsDTO.class);
        when(dto.partnership()).thenReturn(partnership);
        when(dto.period()).thenReturn("2026-10");
        when(billsMapper.asEntity(dto)).thenReturn(new Bills());
        when(billsRepository.save(any(Bills.class))).thenAnswer(invocation -> invocation.getArgument(0));

        billsService.createBills(dto);

        verify(billsRepository).save(any(Bills.class));
    }

    @Test
    void createBills_skipsDuplicateCheckWithoutPartnershipId() {
        var partnership = mock(PartnershipDTO.class);
        when(partnership.id()).thenReturn(null);
        var dto = mock(BillsDTO.class);
        when(dto.partnership()).thenReturn(partnership);
        when(billsMapper.asEntity(dto)).thenReturn(new Bills());
        when(billsRepository.save(any(Bills.class))).thenAnswer(invocation -> invocation.getArgument(0));

        billsService.createBills(dto);

        verify(billsRepository, never()).existsByPartnership_IdAndPeriodAndActiveTrue(any(), any());
    }

    @Test
    void getBillDetails_returnsHeaderWithEnterpriseAndSales() {
        var bills = new Bills();
        bills.setRef("REF-1");
        bills.setPeriod("2026-10");
        bills.setStartDate(LocalDateTime.of(2026, 10, 1, 0, 0));
        bills.setEndDate(LocalDateTime.of(2026, 10, 31, 23, 59, 59));
        bills.setBillStatus(BillStatus.UNPAYED);
        bills.setTotalAmount(12500.0);
        bills.setPartnership(partnership(1L, 10L, 20L));
        when(billsRepository.findById(5L)).thenReturn(Optional.of(bills));

        var details = billsService.getBillDetails(5L);

        assertThat(details).isEqualTo(new BillDetailsDTO("REF-1", "2026-10", LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 10, 31, 23, 59, 59), BillStatus.UNPAYED, 12500.0,
                new BillDetailsDTO.EnterpriseInfo("Sonatel", "Dakar Plateau"),
                new BillDetailsDTO.SalesInfo("Auchan Mermoz", "Mermoz", Modules.MARKET)));
    }

    @Test
    void getBillDetails_throwsWhenNotFound() {
        when(billsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> billsService.getBillDetails(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void generateMonthlyBills_createsOneUnpaidBillPerPartnershipWithPayments() {
        var partnership = partnership(1L, 10L, 20L);
        when(partnershipRepository.findByActiveTrue()).thenReturn(List.of(partnership));
        when(paymentRepository.sumActivePaymentsByEnterpriseAndSales(10L, 20L,
                LocalDateTime.of(2026, 10, 1, 0, 0), LocalDateTime.of(2026, 11, 1, 0, 0))).thenReturn(12500.0);
        when(billsRepository.save(any(Bills.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(billsMapper.asDTO(any(Bills.class))).thenReturn(mock(BillsDTO.class));

        var generated = billsService.generateMonthlyBills(YearMonth.of(2026, 10));

        assertThat(generated).hasSize(1);
        ArgumentCaptor<Bills> saved = ArgumentCaptor.forClass(Bills.class);
        verify(billsRepository).save(saved.capture());
        Bills bill = saved.getValue();
        assertThat(bill.getPartnership()).isSameAs(partnership);
        assertThat(bill.getPeriod()).isEqualTo("2026-10");
        assertThat(bill.getStartDate()).isEqualTo(LocalDateTime.of(2026, 10, 1, 0, 0));
        assertThat(bill.getEndDate()).isEqualTo(LocalDateTime.of(2026, 10, 31, 23, 59, 59));
        assertThat(bill.getTotalAmount()).isEqualTo(12500.0);
        assertThat(bill.getBillStatus()).isEqualTo(BillStatus.UNPAYED);
        assertThat(bill.isActive()).isTrue();
        assertThat(bill.getRef()).isNotBlank();
    }

    @Test
    void generateMonthlyBills_skipsExistingBillsAndPartnershipsWithoutPayments() {
        var alreadyBilled = partnership(1L, 10L, 20L);
        var noPayment = partnership(2L, 11L, 21L);
        var nullSum = partnership(3L, 12L, 22L);
        when(partnershipRepository.findByActiveTrue()).thenReturn(List.of(alreadyBilled, noPayment, nullSum));
        when(billsRepository.existsByPartnership_IdAndPeriodAndActiveTrue(1L, "2026-12")).thenReturn(true);
        when(paymentRepository.sumActivePaymentsByEnterpriseAndSales(eq(11L), eq(21L), any(), any())).thenReturn(0.0);
        when(paymentRepository.sumActivePaymentsByEnterpriseAndSales(eq(12L), eq(22L), any(), any())).thenReturn(null);

        var generated = billsService.generateMonthlyBills(YearMonth.of(2026, 12));

        assertThat(generated).isEmpty();
        verify(billsRepository, never()).save(any());
        verify(paymentRepository, never()).sumActivePaymentsByEnterpriseAndSales(eq(10L), eq(20L), any(), any());
        verify(paymentRepository).sumActivePaymentsByEnterpriseAndSales(11L, 21L,
                LocalDateTime.of(2026, 12, 1, 0, 0), LocalDateTime.of(2027, 1, 1, 0, 0));
    }

    private static Partnership partnership(Long id, Long enterpriseId, Long salesId) {
        var enterprise = new Enterprise();
        enterprise.setId(enterpriseId);
        enterprise.setName("Sonatel");
        enterprise.setAddress("Dakar Plateau");
        var sales = new Sales();
        sales.setId(salesId);
        sales.setName("Auchan Mermoz");
        sales.setAddress("Mermoz");
        sales.setType(Modules.MARKET);
        var partnership = new Partnership();
        partnership.setId(id);
        partnership.setEnterprise(enterprise);
        partnership.setSales(sales);
        return partnership;
    }
}
