package com.sn.onepay.repository;

import com.sn.onepay.entity.QRCodeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QRCodeClientRepository extends JpaRepository<QRCodeClient, Long>, QuerydslPredicateExecutor<QRCodeClient> {
    Optional<QRCodeClient> findByRef(String ref);
}
