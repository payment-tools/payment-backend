package com.sn.onepay.entity;


import com.sn.onepay.enumeration.BillStatus;
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
@Table(name = "Bills")
@TableGenerator(name = "BillsGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "BillsId", allocationSize = 1)
public class Bills {

    @Id
    @Column(name = "BillsId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BillsGen")
    Long id;

    @Column(name = "Ref")
    String ref;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PartnershipId")
    Partnership partnership;

    @Column(name = "StartDate", nullable = false)
    LocalDateTime startDate;

    @Column(name = "EndDate", nullable = false)
    LocalDateTime endDate;

    @Column(name = "TotalAmount", nullable = false)
    Double totalAmount;

    @Column(name = "BillStatus", nullable = false)
    @Enumerated(EnumType.STRING)
    BillStatus billStatus;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "ModificationDate", insertable = false)
    LocalDateTime modificationDate;

}
