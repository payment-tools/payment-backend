package com.sn.onepay.repository;

import com.sn.onepay.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientUsername(String recipientUsername, Pageable pageable);

    Page<Notification> findByRecipientUsernameAndRead(String recipientUsername, boolean read, Pageable pageable);

    long countByRecipientUsernameAndReadFalse(String recipientUsername);

    Optional<Notification> findByIdAndRecipientUsername(Long id, String recipientUsername);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.recipientUsername = :username AND n.read = false")
    int markAllAsRead(@Param("username") String username);
}
