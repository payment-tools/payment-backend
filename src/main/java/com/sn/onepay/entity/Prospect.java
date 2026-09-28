package com.sn.onepay.entity;

import com.sn.onepay.enumeration.ProspectScore;
import com.sn.onepay.enumeration.ProspectStage;
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

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Prospect")
@TableGenerator(name = "ProspectGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "ProspectId", allocationSize = 1)
public class Prospect {

    @Id
    @Column(name = "ProspectId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ProspectGen")
    Long id;

    @Column(name = "Ref")
    String ref;

    @Column(name = "CompanyName", nullable = false)
    String companyName;

    @Column(name = "ContactName")
    String contactName;

    @Column(name = "Email")
    String email;

    @Column(name = "PhoneNumber")
    String phoneNumber;

    @Column(name = "Stage", nullable = false)
    @Enumerated(EnumType.STRING)
    ProspectStage stage;

    @Column(name = "Note")
    String note;

    @Column(name = "PotentialEmployees")
    Integer potentialEmployees;

    @Column(name = "AssignedTo")
    String assignedTo;

    @Column(name = "Score", nullable = false)
    @Enumerated(EnumType.STRING)
    ProspectScore score;

    @Column(name = "Active", nullable = false)
    boolean active;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "ModificationDate", insertable = false)
    LocalDateTime modificationDate;

}
