package com.sn.onepay.entity;

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
@Table(name = "Payment")
@TableGenerator(name = "PaymentGen", table = "JPA_SEQUENCE", pkColumnName = "SEQ_KEY", valueColumnName = "SEQ_VALUE", pkColumnValue = "PaymentId", allocationSize = 1)
public class Payment {

    @Id
    @Column(name = "PaymentId", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PaymentGen")
    Long id;

    @Column(name = "Ref")
    String ref;

    @OneToOne
    Client client;

    @OneToOne
    Cashier cashier;

    @OneToOne
    Partnership partnership;

    @Column(name = "Amount", nullable = false)
    Double amount;

    @Column(name = "Status", nullable = false)
    @Enumerated(EnumType.STRING)
    StateStatus status;

    @Column(name = "Date", nullable = false)
    LocalDateTime date;

    @CreatedDate
    @Column(name = "CreationDate", updatable = false)
    LocalDateTime creationDate;

    @LastModifiedDate
    @Column(name = "ModificationDate", insertable = false)
    LocalDateTime modificationDate;
}
