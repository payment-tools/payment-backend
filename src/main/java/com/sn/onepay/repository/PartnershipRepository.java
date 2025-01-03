package com.sn.onepay.repository;

import com.sn.onepay.entity.Partnership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnershipRepository extends JpaRepository<Partnership, Long>, QuerydslPredicateExecutor<Partnership> {
    Partnership findPartnershipBySalesIdAndEnterpriseId(Long salesId, Long enterpriseId);
}
