package com.sn.onepay.entity;

import com.sn.onepay.enumeration.Roles;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SalesProfile")
@TableGenerator(name = "SalesProfileGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "SalesProfileId", allocationSize = 1)
public class SalesProfile {

    @Id
    @Column(name = "SalesProfileId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SalesProfileGen")
    Long id;

    @Column(name = "Ref")
    String ref;

    @Column(name = "Firstname", nullable = false)
    String firstname;

    @Column(name = "Lastname", nullable = false)
    String lastname;

    @Column(name = "Username", nullable = false, unique = true)
    String username;

    @Column(name = "Email", nullable = false, unique = true)
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
