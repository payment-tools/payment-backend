package com.sn.onepay.repository;

import com.sn.onepay.entity.EnterpriseConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterpriseConfigurationRepository extends JpaRepository<EnterpriseConfiguration, Long>, QuerydslPredicateExecutor<EnterpriseConfiguration> {
    EnterpriseConfiguration getEnterpriseConfigurationByEnterpriseId(Long enterpriseId);
}
