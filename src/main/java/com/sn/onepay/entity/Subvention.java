package com.sn.onepay.entity;

import com.sn.onepay.enumeration.StateStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Subvention")
@TableGenerator(name = "SubventionGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "SubventionId", allocationSize = 1)
public class Subvention {

    @Id
    @Column(name = "SubventionId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SubventionGen")
    Long id;

    @Column(name = "Ref")
    String ref;

    @Column(name = "EmployeePercent", nullable = false)
    Double employeePercent;

    @Column(name = "EmployerPercent", nullable = false)
    Double employerPercent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PartnershipId")
    Partnership partnership;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EmployeeGroupId")
    EmployeeGroup employeeGroup;

    @Column(name = "Status", nullable = false)
    @Enumerated(EnumType.STRING)
    StateStatus status;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "ModificationDate", insertable = false)
    LocalDateTime modificationDate;
}