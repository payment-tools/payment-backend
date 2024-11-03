package com.sn.onepay.entity;


import com.sn.onepay.enumeration.Modules;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Table(name = "Enterprise")
@TableGenerator(name = "EnterpriseGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "EnterpriseId", allocationSize = 1)
public class Enterprise {

    @Id
    @Column(name = "EnterpriseId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EnterpriseGen")
    Long id;

    @Column(name = "Ref")
    String ref;

    @Column(name = "Name", nullable = false)
    String name;

    @Column(name = "MaxQuota", nullable = false)
    Long maxQuota;

    @Column(name = "ActualQuota", nullable = false)
    Long actualQuota;

    @Column(name = "EnrolledModules")
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "EnrolledModules", joinColumns = @JoinColumn(name = "EnterpriseId"))
    Collection<Modules> enrolledModules;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "ModificationDate", insertable = false)
    LocalDateTime modificationDate;

}
