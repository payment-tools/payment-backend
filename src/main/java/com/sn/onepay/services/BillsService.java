package com.sn.onepay.services;

import com.sn.onepay.dto.BillDetailsDTO;
import com.sn.onepay.dto.BillsDTO;
import com.sn.onepay.enumeration.BillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public interface BillsService {

    BillsDTO createBills(BillsDTO billsDTO);

    BillsDTO updateBills(BillsDTO billsDTO, Long billsId);

    void deleteBills(Long billsId);

    Page<BillsDTO> getBillsByFilters(Long id, String ref, Long partnershipId, BillStatus billStatus, String period, Boolean active, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);

    BillsDTO getBillsById(Long id);

    /*En-tête de la facture, avec l'entreprise et le commerce, pour la génération du PDF par le frontend*/
    BillDetailsDTO getBillDetails(Long id);

    /*Une facture par partenariat actif ayant des paiements sur le mois ; aucune si elle existe déjà (idempotent)*/
    List<BillsDTO> generateMonthlyBills(YearMonth month);
}
