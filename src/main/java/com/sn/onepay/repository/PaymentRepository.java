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

    @Query(value = "SELECT SUM(p.amount) FROM Payment p WHERE p.clientId= :clientId AND p.module= :module AND p.status ='ACTIVE'", nativeQuery = true)
    Double findAllPaymentsByClientIdAndModule(@Param ("clientId") Long clientId, @Param("module") String module);

    List<Payment> getAllPaymentsByClientId(Long clientId);
}
