package com.sn.onepay.entity;

import com.sn.onepay.enumeration.Modules;
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
@Table(name = "Sales")
@TableGenerator(name = "SalesGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "SalesId", allocationSize = 1)
public class Sales {

    @Id
    @Column(name = "SalesId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SalesGen")
    Long id;

    @Column(name = "Ref")
    String ref;

    @Column(name = "Name", nullable = false)
    String name;

    @Column(name = "Type", nullable = false)
    @Enumerated(EnumType.STRING)
    Modules type;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "ModificationDate", insertable = false)
    LocalDateTime modificationDate;

}
