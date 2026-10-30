package com.sn.onepay.repository;

import com.sn.onepay.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    Optional<DeviceToken> findByToken(String token);

    List<DeviceToken> findByUsername(String username);

    long deleteByTokenAndUsername(String token, String username);

    long deleteByTokenIn(Collection<String> tokens);
}
