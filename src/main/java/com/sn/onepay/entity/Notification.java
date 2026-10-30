package com.sn.onepay.entity;

import com.sn.onepay.enumeration.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/*Historique des notifications d'un utilisateur (cloche de l'app) : créée par le système, jamais via l'API*/
@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Notification")
@TableGenerator(name = "NotificationGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "NotificationId", allocationSize = 1)
public class Notification {

    @Id
    @Column(name = "NotificationId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "NotificationGen")
    Long id;

    @Column(name = "RecipientUsername", nullable = false)
    String recipientUsername;

    @Column(name = "Type", nullable = false)
    @Enumerated(EnumType.STRING)
    NotificationType type;

    @Column(name = "Title", nullable = false)
    String title;

    @Column(name = "Message", nullable = false)
    String message;

    @Column(name = "PaymentId")
    Long paymentId;

    @Column(name = "Read", nullable = false)
    boolean read;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;
}
