package com.sn.onepay.repository;

import com.sn.onepay.entity.SalesProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesProfileRepository extends JpaRepository<SalesProfile, Long>, QuerydslPredicateExecutor<SalesProfile> {
}
