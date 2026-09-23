package com.sn.onepay.repository;

import com.sn.onepay.entity.Prospect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProspectRepository extends JpaRepository<Prospect, Long>, QuerydslPredicateExecutor<Prospect> {
}
