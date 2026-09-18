package com.sn.onepay.repository;

import com.sn.onepay.entity.EmployeeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeGroupRepository extends JpaRepository<EmployeeGroup, Long>, QuerydslPredicateExecutor<EmployeeGroup> {

    List<EmployeeGroup> findByActiveTrueAndClients_Id(Long clientId);
}