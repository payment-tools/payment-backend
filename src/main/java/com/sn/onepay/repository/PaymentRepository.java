package com.sn.onepay.repository;

import com.sn.onepay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, QuerydslPredicateExecutor<Payment> {

    /*Paiements actifs dont la date est dans [from, to[ (bornes du mois en cours pour les cumuls)*/
    @Query(value = "SELECT COALESCE(SUM(p.Amount), 0) FROM Payment p WHERE p.ClientId = :clientId AND p.Module = :module AND p.Active = true " +
            "AND p.PaymentDate >= :from AND p.PaymentDate < :to", nativeQuery = true)
    Double findAllPaymentsByClientIdAndModule(@Param("clientId") Long clientId, @Param("module") String module,
                                              @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT COALESCE(SUM(p.Amount), 0) FROM Payment p WHERE p.ClientId = :clientId AND p.Active = true " +
            "AND p.PaymentDate >= :from AND p.PaymentDate < :to", nativeQuery = true)
    Double findSumOfAllActivePaymentsByClientId(@Param("clientId") Long clientId,
                                                @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    List<Payment> getAllPaymentsByClientId(Long clientId);

    /*Montant facturé à un partenariat : paiements actifs des employés de l'entreprise chez les caissiers du commerce, sur [from, to[*/
    @Query(value = "SELECT COALESCE(SUM(p.Amount), 0) FROM Payment p " +
            "JOIN Client c ON p.ClientId = c.ClientId JOIN Cashier k ON p.CashierId = k.CashierId " +
            "WHERE p.Active = true AND c.EnterpriseId = :enterpriseId AND k.SalesId = :salesId " +
            "AND p.PaymentDate >= :from AND p.PaymentDate < :to", nativeQuery = true)
    Double sumActivePaymentsByEnterpriseAndSales(@Param("enterpriseId") Long enterpriseId, @Param("salesId") Long salesId,
                                                 @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT TO_CHAR(p.PaymentDate, 'YYYY-MM') AS month, p.Module AS module, COALESCE(SUM(p.Amount), 0) AS totalAmount " +
            "FROM Payment p JOIN Client c ON p.ClientId = c.ClientId " +
            "WHERE c.EnterpriseId = :enterpriseId AND p.Active = true AND p.PaymentDate >= :fromDate " +
            "GROUP BY TO_CHAR(p.PaymentDate, 'YYYY-MM'), p.Module " +
            "ORDER BY month", nativeQuery = true)
    List<PaymentMonthlyModuleSum> findMonthlySumByEnterpriseId(@Param("enterpriseId") Long enterpriseId, @Param("fromDate") LocalDateTime fromDate);
}
