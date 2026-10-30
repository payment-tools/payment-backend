package com.sn.onepay.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.sn.onepay.dto.BillDetailsDTO;
import com.sn.onepay.dto.BillsDTO;
import com.sn.onepay.entity.Bills;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.Partnership;
import com.sn.onepay.entity.QBills;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.BillStatus;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.exceptions.ObjectValidationException;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.BillsMapper;
import com.sn.onepay.repository.BillsRepository;
import com.sn.onepay.repository.EnterpriseProfileRepository;
import com.sn.onepay.repository.PartnershipRepository;
import com.sn.onepay.repository.PaymentRepository;
import com.sn.onepay.repository.SalesProfileRepository;
import com.sn.onepay.services.BillsService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class BillsServiceImpl implements BillsService {

    final BillsRepository billsRepository;
    final BillsMapper billsMapper;
    final PartnershipRepository partnershipRepository;
    final PaymentRepository paymentRepository;
    final EnterpriseProfileRepository enterpriseProfileRepository;
    final SalesProfileRepository salesProfileRepository;
    final Clock clock;

    @Override
    public BillsDTO createBills(BillsDTO billsDTO) {

        // Une seule facture active par partenariat et par mois (index unique en base)
        if (billsDTO.partnership() != null && billsDTO.partnership().id() != null
                && billsRepository.existsByPartnership_IdAndPeriodAndActiveTrue(billsDTO.partnership().id(), billsDTO.period())) {
            throw new ResourceAlreadyExistException("Une facture existe déjà pour ce partenariat et cette période : " + billsDTO.period());
        }

        Bills bills = billsMapper.asEntity(billsDTO);
        bills.setRef(UUID.randomUUID().toString());
        bills.setActive(true);
        // Le statut ne se choisit pas : une facture naît UNPAYED, seules les deux confirmations la font passer à PAYED
        bills.setBillStatus(BillStatus.UNPAYED);
        bills.setEnterprisePaymentConfirmedBy(null);
        bills.setEnterprisePaymentConfirmedAt(null);
        bills.setSalesPaymentConfirmedBy(null);
        bills.setSalesPaymentConfirmedAt(null);
        var savedBills = billsRepository.save(bills);

        log.info("Created new Bills: {}", savedBills);
        log.trace("Created new Bills with id: {}", savedBills.getId());

        return billsMapper.asDTO(savedBills);
    }

    @Override
    public BillsDTO updateBills(BillsDTO billsDTO, Long billsId) {

        Bills existing = billsRepository.findById(billsId)
                .orElseThrow(() -> new ResourceNotFoundException("Bills", "ID", billsId));

        if (billsDTO.startDate() != null) existing.setStartDate(billsDTO.startDate());
        if (billsDTO.endDate() != null) existing.setEndDate(billsDTO.endDate());
        if (billsDTO.totalAmount() != null) existing.setTotalAmount(billsDTO.totalAmount());
        // Statut et confirmations ignorés : ils ne changent que par /payment-confirmation
        if (billsDTO.period() != null) existing.setPeriod(billsDTO.period());
        if (billsDTO.active() != null) existing.setActive(billsDTO.active());

        var updated = billsRepository.saveAndFlush(existing);

        log.info("Updated Bills: {}", updated);
        log.trace("Updated Bills with id: {}", updated.getId());

        return billsMapper.asDTO(updated);
    }

    @Override
    public void deleteBills(Long billsId) {

        /*Implements logical deletion*/
        Bills bills = billsRepository.findById(billsId).orElseThrow(() -> new ResourceNotFoundException("Bills", "ID", billsId));
        bills.setActive(false);
        billsRepository.saveAndFlush(bills);

        log.info("Deleted Bills: {}", billsId);
        log.trace("Deleted Bills with id: {}", billsId);
    }

    @Override
    public Page<BillsDTO> getBillsByFilters(Long id, String ref, Long partnershipId, BillStatus billStatus, String period, Boolean active, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable) {

        QBills bills = QBills.bills;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(bills.id.eq(id));
        }
        if (ref != null && !ref.isEmpty()) {
            builder.and(bills.ref.containsIgnoreCase(ref));
        }
        if (partnershipId != null) {
            builder.and(bills.partnership.id.eq(partnershipId));
        }
        if (billStatus != null) {
            builder.and(bills.billStatus.eq(billStatus));
        }
        if (period != null && !period.isEmpty()) {
            builder.and(bills.period.eq(period));
        }
        if (active != null) {
            builder.and(bills.active.eq(active));
        }
        if (startDate != null) {
            builder.and(bills.startDate.goe(startDate));
        }
        if (endDate != null) {
            builder.and(bills.endDate.loe(endDate));
        }
        if (creationDate != null) {
            builder.and(bills.creationDate.goe(creationDate));
        }
        if (modificationDate != null) {
            builder.and(bills.modificationDate.loe(modificationDate));
        }

        Page<Bills> result = billsRepository.findAll(builder, pageable);
        return result.map(billsMapper::asDTO);
    }

    @Override
    public BillsDTO getBillsById(Long id) {
        return billsMapper.asDTO(billsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bills", "ID", id)));
    }

    @Override
    public BillDetailsDTO getBillDetails(Long id) {
        Bills bills = billsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bills", "ID", id));
        Enterprise enterprise = bills.getPartnership().getEnterprise();
        Sales sales = bills.getPartnership().getSales();
        return new BillDetailsDTO(bills.getRef(), bills.getPeriod(), bills.getStartDate(), bills.getEndDate(),
                bills.getBillStatus(), bills.getTotalAmount(),
                new BillDetailsDTO.EnterpriseInfo(enterprise.getName(), enterprise.getAddress()),
                new BillDetailsDTO.SalesInfo(sales.getName(), sales.getAddress(), sales.getType()));
    }

    @Override
    public List<BillsDTO> generateMonthlyBills(YearMonth month) {
        String period = month.toString();
        LocalDateTime from = month.atDay(1).atStartOfDay();
        LocalDateTime to = month.plusMonths(1).atDay(1).atStartOfDay();

        List<BillsDTO> generated = new ArrayList<>();
        for (Partnership partnership : partnershipRepository.findByActiveTrue()) {
            if (billsRepository.existsByPartnership_IdAndPeriodAndActiveTrue(partnership.getId(), period)) {
                continue;
            }
            Double total = paymentRepository.sumActivePaymentsByEnterpriseAndSales(
                    partnership.getEnterprise().getId(), partnership.getSales().getId(), from, to);
            if (total == null || total <= 0) {
                continue;
            }

            Bills bills = new Bills();
            bills.setRef(UUID.randomUUID().toString());
            bills.setPartnership(partnership);
            bills.setPeriod(period);
            bills.setStartDate(from);
            bills.setEndDate(month.atEndOfMonth().atTime(23, 59, 59));
            bills.setTotalAmount(total);
            bills.setBillStatus(BillStatus.UNPAYED);
            bills.setActive(true);
            generated.add(billsMapper.asDTO(billsRepository.save(bills)));
        }

        log.info("Generated {} bill(s) for period {}", generated.size(), period);
        return generated;
    }

    @Override
    public BillsDTO confirmPayment(Long billsId, String username, Collection<String> roles) {
        Bills bills = findActiveBill(billsId);
        if (bills.getBillStatus() == BillStatus.PAYED) {
            throw new ObjectValidationException("La facture est déjà payée");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        if (resolveSide(bills, username, roles) == PaymentSide.ENTERPRISE) {
            if (bills.getEnterprisePaymentConfirmedBy() == null) {
                bills.setEnterprisePaymentConfirmedBy(username);
                bills.setEnterprisePaymentConfirmedAt(now);
            }
        } else if (bills.getSalesPaymentConfirmedBy() == null) {
            bills.setSalesPaymentConfirmedBy(username);
            bills.setSalesPaymentConfirmedAt(now);
        }
        if (bills.getEnterprisePaymentConfirmedBy() != null && bills.getSalesPaymentConfirmedBy() != null) {
            bills.setBillStatus(BillStatus.PAYED);
        }
        Bills saved = billsRepository.saveAndFlush(bills);
        log.info("Payment of bill {} confirmed by {} (status {})", billsId, username, saved.getBillStatus());
        return billsMapper.asDTO(saved);
    }

    @Override
    public BillsDTO cancelPaymentConfirmation(Long billsId, String username, Collection<String> roles) {
        Bills bills = findActiveBill(billsId);
        // Une fois les deux confirmations reçues, la facture est PAYED : plus de retour arrière
        if (bills.getBillStatus() == BillStatus.PAYED) {
            throw new ObjectValidationException("Une facture payée ne peut plus être modifiée");
        }
        if (resolveSide(bills, username, roles) == PaymentSide.ENTERPRISE) {
            bills.setEnterprisePaymentConfirmedBy(null);
            bills.setEnterprisePaymentConfirmedAt(null);
        } else {
            bills.setSalesPaymentConfirmedBy(null);
            bills.setSalesPaymentConfirmedAt(null);
        }
        Bills saved = billsRepository.saveAndFlush(bills);
        log.info("Payment confirmation of bill {} cancelled by {}", billsId, username);
        return billsMapper.asDTO(saved);
    }

    private enum PaymentSide { ENTERPRISE, SALES }

    private Bills findActiveBill(Long billsId) {
        return billsRepository.findById(billsId)
                .filter(Bills::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Bills", "ID", billsId));
    }

    /*Côté de l'appelant : finance de l'entreprise ou finance du commerce, et seulement celle du partenariat de la facture*/
    private PaymentSide resolveSide(Bills bills, String username, Collection<String> roles) {
        if (roles.contains(Roles.ENTERPRISE_FINANCE.name())) {
            Long enterpriseId = bills.getPartnership().getEnterprise().getId();
            boolean member = enterpriseProfileRepository.findByUsername(username)
                    .filter(profile -> profile.isActive() && profile.getRole() == Roles.ENTERPRISE_FINANCE)
                    .filter(profile -> profile.getEnterprise() != null && enterpriseId.equals(profile.getEnterprise().getId()))
                    .isPresent();
            if (member) {
                return PaymentSide.ENTERPRISE;
            }
        } else if (roles.contains(Roles.SALES_FINANCE.name())) {
            Long salesId = bills.getPartnership().getSales().getId();
            boolean member = salesProfileRepository.findByUsername(username)
                    .filter(profile -> profile.isActive() && profile.getRole() == Roles.SALES_FINANCE)
                    .filter(profile -> profile.getSales() != null && salesId.equals(profile.getSales().getId()))
                    .isPresent();
            if (member) {
                return PaymentSide.SALES;
            }
        }
        throw new AccessDeniedException("Seule la finance de l'entreprise ou du commerce de ce partenariat peut confirmer le paiement de cette facture");
    }
}
