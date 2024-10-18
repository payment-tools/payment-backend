package com.sn.onepay.entity;

import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
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
@Table(name = "SalesProfile")
@TableGenerator(name = "SalesProfileGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "SalesProfileId", allocationSize = 1)

public class SalesProfile {

    @Id
    @Column(name = "SalesProfileId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SalesProfileGen")
    Long id;

    @Column(name = "Ref")
    String ref;

    @Column(name = "Firstname", nullable = false)
    String firstname;

    @Column(name = "Lastname", nullable = false)
    String lastname;

    @Column(name = "Username", nullable = false)
    String username;

    @Column(name = "Email", nullable = false)
    String email;

    @Column(name = "PhoneNumber")
    String phoneNumber;

    @Column(name = "Role", nullable = false)
    @Enumerated(EnumType.STRING)
    Roles role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SalesId")
    Sales sales;

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
