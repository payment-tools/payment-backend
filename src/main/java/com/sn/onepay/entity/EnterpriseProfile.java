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
@Table(name = "EnterpriseProfile")
@TableGenerator(name = "EnterpriseProfileGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "EnterpriseProfileId", allocationSize = 1)
public class EnterpriseProfile {

    @Id
    @Column(name = "EnterpriseProfileId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "EnterpriseProfileGen")
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
    @JoinColumn(name = "EnterpriseId")
    Enterprise enterprise;

    @Column(name = "Active", nullable = false)
    boolean active;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "ModificationDate", insertable = false)
    LocalDateTime modificationDate;

}
