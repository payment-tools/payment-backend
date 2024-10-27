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
@Table(name = "EnterpriseConfiguration")
@TableGenerator(name = "EnterpriseConfigurationGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "EnterpriseConfigurationId", allocationSize = 1)
public class EnterpriseConfiguration {

    @Id
    @Column(name = "EnterpriseConfigurationId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EnterpriseConfigurationGen")
    Long id;

    @OneToOne
    Enterprise enterprise;

    @Column(name = "MaxAmountRestauration")
    Double maxAmountRestauration;


    @Column(name = "MaxAmountMarket")
    Double maxAmountMarket;

    @Column(name = "MaxAmountGasStation")
    Double maxAmountGasStation;

    @Column(name = "MaxAmountTelephony")
    Double maxAmountTelephony;

    @Column(name = "EnterprisePercentage", nullable = false)
    int enterprisePercentage;

    @Column(name = "EmployeePercentage", nullable = false)
    int employeePercentage;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "ModificationDate", insertable = false)
    LocalDateTime modificationDate;

}
