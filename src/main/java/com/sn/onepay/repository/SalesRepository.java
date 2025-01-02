package com.sn.onepay.repository;

import com.sn.onepay.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long>, QuerydslPredicateExecutor<Sales> {
}
