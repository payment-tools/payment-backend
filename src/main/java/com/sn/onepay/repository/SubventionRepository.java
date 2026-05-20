package com.sn.onepay.repository;

import com.sn.onepay.entity.Subvention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubventionRepository extends JpaRepository<Subvention, Long>, QuerydslPredicateExecutor<Subvention> {
}