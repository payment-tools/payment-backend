package com.sn.onepay.repository;

import com.sn.onepay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, QuerydslPredicateExecutor<Payment> {
}
