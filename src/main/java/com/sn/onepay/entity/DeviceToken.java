package com.sn.onepay.entity;

import com.sn.onepay.enumeration.DevicePlatform;
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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/*Jeton FCM d'un appareil mobile, rattaché à l'utilisateur connecté (plusieurs appareils possibles)*/
@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
@Table(name = "DeviceToken")
@TableGenerator(name = "DeviceTokenGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "DeviceTokenId", allocationSize = 1)
public class DeviceToken {

    @Id
    @Column(name = "DeviceTokenId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "DeviceTokenGen")
    Long id;

    @Column(name = "Username", nullable = false)
    String username;

    @Column(name = "Token", nullable = false, unique = true)
    String token;

    @Column(name = "Platform", nullable = false)
    @Enumerated(EnumType.STRING)
    DevicePlatform platform;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "ModificationDate", insertable = false)
    LocalDateTime modificationDate;
}
