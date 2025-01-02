package com.sn.onepay.repository;

import com.sn.onepay.entity.EnterpriseProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterpriseProfileRepository extends JpaRepository<EnterpriseProfile, Long>, QuerydslPredicateExecutor<EnterpriseProfile> {
}
