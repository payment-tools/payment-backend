package com.sn.onepay.entity;

import com.sn.onepay.enumeration.StateStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.time.LocalDateTime;


@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Table(name = "Partnership")
@TableGenerator(name = "PartnershipGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "PartnershipId", allocationSize = 1)
public class Partnership {

    @Id
    @Column(name = "PartnershipId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PartnershipGen")
    Long id;

    @Column(name = "Ref")
    String ref;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SalesId")
    Sales sales;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EnterpriseId")
    Enterprise enterprise;

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
