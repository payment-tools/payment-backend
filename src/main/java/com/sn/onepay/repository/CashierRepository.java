package com.sn.onepay.repository;

import com.sn.onepay.entity.Cashier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CashierRepository extends JpaRepository<Cashier, Long>, QuerydslPredicateExecutor<Cashier> {
}
