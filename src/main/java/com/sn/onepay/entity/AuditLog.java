package com.sn.onepay.entity;

import com.sn.onepay.enumeration.AuditSeverity;
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

/*Append-only system record: written internally by AuditLoggingInterceptor, never created/updated through the API*/
@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
@Table(name = "AuditLog")
@TableGenerator(name = "AuditLogGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "AuditLogId", allocationSize = 1)
public class AuditLog {

    @Id
    @Column(name = "AuditLogId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "AuditLogGen")
    Long id;

    @Column(name = "EventType", nullable = false)
    String eventType;

    @Column(name = "HttpMethod")
    String httpMethod;

    @Column(name = "Path")
    String path;

    @Column(name = "ActorUsername")
    String actorUsername;

    @Column(name = "ActorRole")
    String actorRole;

    @Column(name = "IpAddress")
    String ipAddress;

    @Column(name = "Severity", nullable = false)
    @Enumerated(EnumType.STRING)
    AuditSeverity severity;

    @Column(name = "Details")
    String details;

    @Column(name = "Active", nullable = false)
    boolean active;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "ModificationDate", insertable = false)
    LocalDateTime modificationDate;

}
