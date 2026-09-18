package com.sn.onepay.repository;

import com.sn.onepay.entity.Subvention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubventionRepository extends JpaRepository<Subvention, Long>, QuerydslPredicateExecutor<Subvention> {

    List<Subvention> findByActiveTrueAndPartnership_IdAndEmployeeGroup_IdIn(Long partnershipId, List<Long> employeeGroupIds);
}