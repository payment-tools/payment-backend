package com.sn.onepay.entity;

import com.sn.onepay.enumeration.StateStatus;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = {"clients", "enterprise"})
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EmployeeGroup")
@TableGenerator(name = "EmployeeGroupGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "EmployeeGroupId", allocationSize = 1)
public class EmployeeGroup {

    @Id
    @Column(name = "EmployeeGroupId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "EmployeeGroupGen")
    Long id;

    @Column(name = "Ref")
    String ref;

    @Column(name = "Name", nullable = false)
    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EnterpriseId")
    Enterprise enterprise;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "EmployeeGroupClient",
            joinColumns = @JoinColumn(name = "EmployeeGroupId"),
            inverseJoinColumns = @JoinColumn(name = "ClientId")
    )
    List<Client> clients;

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