package com.sn.onepay.entity;

import jakarta.persistence.*;
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
@Table(name = "SalesConfigurations")
@TableGenerator(name = "SalesConfigurationsGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "SalesConfigurationsId", allocationSize = 1)
public class SalesConfigurations {

    @Id
    @Column(name = "SalesConfigurationsId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SalesConfigurationsGen")
    Long id;

    @OneToOne
    Sales sales;

    @Column(name = "MinAmount")
    Double minAmount;

    @Column(name = "MaxAmount")
    Double maxAmount;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "ModificationDate", insertable = false)
    LocalDateTime modificationDate;

}
