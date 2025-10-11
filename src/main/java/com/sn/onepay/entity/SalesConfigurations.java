package com.sn.onepay.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "SalesConfigurations")
@TableGenerator(name = "SalesConfigurationsGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "SalesConfigurationsId", allocationSize = 1)
public class SalesConfigurations {

    @Id
    @Column(name = "salesConfigurationsId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SalesConfigurationsGen")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesId")
    Sales sales;

    @Column(name = "minAmount")
    Double minAmount;

    @Column(name = "maxAmount")
    Double maxAmount;

    @CreatedDate
    @Column(name = "creationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "modificationDate", insertable = false)
    LocalDateTime modificationDate;

}
