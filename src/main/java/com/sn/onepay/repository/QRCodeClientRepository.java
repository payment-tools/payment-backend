package com.sn.onepay.repository;

import com.sn.onepay.entity.QRCodeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface QRCodeClientRepository extends JpaRepository<QRCodeClient, Long>, QuerydslPredicateExecutor<QRCodeClient> {
    Optional<QRCodeClient> findByRef(String ref);

    @Modifying
    @Query("UPDATE QRCodeClient q SET q.used = true WHERE q.ref = :ref AND q.client.id = :clientId AND q.active = true AND q.used = false AND q.expirationDate > :now")
    int consume(@Param("ref") String ref, @Param("clientId") Long clientId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE QRCodeClient q SET q.active = false WHERE q.active = true AND q.used = false AND q.expirationDate <= :now")
    int deactivateExpired(@Param("now") LocalDateTime now);
}
