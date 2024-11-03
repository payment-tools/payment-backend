package com.sn.onepay.repository;

import com.sn.onepay.entity.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, Long>, QuerydslPredicateExecutor<Enterprise> {
}
