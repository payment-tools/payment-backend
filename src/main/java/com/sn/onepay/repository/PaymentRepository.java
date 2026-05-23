package com.sn.onepay.repository;

import com.sn.onepay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, QuerydslPredicateExecutor<Payment> {

    @Query(value = "SELECT COALESCE(SUM(p.Amount), 0) FROM Payment p WHERE p.ClientId = :clientId AND p.Module = :module AND p.Active = true", nativeQuery = true)
    Double findAllPaymentsByClientIdAndModule(@Param("clientId") Long clientId, @Param("module") String module);

    @Query(value = "SELECT COALESCE(SUM(p.Amount), 0) FROM Payment p WHERE p.ClientId = :clientId AND p.Active = true", nativeQuery = true)
    Double findSumOfAllActivePaymentsByClientId(@Param("clientId") Long clientId);

    List<Payment> getAllPaymentsByClientId(Long clientId);
}
