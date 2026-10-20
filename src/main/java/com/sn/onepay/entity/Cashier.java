package com.sn.onepay.entity;

import com.sn.onepay.enumeration.Roles;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Cashier")
@TableGenerator(name = "CashierGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "CashierId", allocationSize = 1)
public class Cashier {

    @Id
    @Column(name = "CashierId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CashierGen")
    Long id;

    @Column(name = "Ref")
    String ref;

    @Column(name = "Firstname")
    String firstname;

    @Column(name = "Lastname")
    String lastname;

    @Column(name = "Username", unique = true, nullable = false)
    String username;

    @Column(name = "Email", unique = true)
    String email;

    @Column(name = "PhoneNumber", unique = true)
    String phoneNumber;

    /*Facultatif : une valeur vide est enregistrée NULL, sinon deux fiches sans téléphone
      se heurteraient à la contrainte d'unicité*/
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = StringUtils.hasText(phoneNumber) ? phoneNumber.trim() : null;
    }

    @Column(name = "Role", nullable = false)
    @Enumerated(EnumType.STRING)
    Roles role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SalesId")
    Sales sales;

    @Column(name = "Active", nullable = false)
    boolean active;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "ModificationDate", insertable = false)
    LocalDateTime modificationDate;

}
