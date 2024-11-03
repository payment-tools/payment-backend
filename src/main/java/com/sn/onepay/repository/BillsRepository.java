package com.sn.onepay.repository;

import com.sn.onepay.entity.Bills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BillsRepository extends JpaRepository<Bills, Long>, QuerydslPredicateExecutor<Bills> {
}
