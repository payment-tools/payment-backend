package com.sn.onepay.repository;

import com.sn.onepay.entity.SalesConfigurations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesConfigurationsRepository extends JpaRepository<SalesConfigurations, Long>, QuerydslPredicateExecutor<SalesConfigurations> {
}
