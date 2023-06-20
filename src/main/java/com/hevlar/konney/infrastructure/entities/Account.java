package com.hevlar.konney.infrastructure.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hevlar.konney.domain.entities.IAccount;
import com.hevlar.konney.domain.valueobjects.AccountGroup;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Account implements IAccount {
    @Id
    String accountId;
    String accountName;
    AccountGroup accountGroup;
    LocalDate openingDate;
    String currency;
    BigDecimal openingBalance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "label", scope = Book.class)
    @ToString.Exclude
    Book book;
}
