package com.hevlar.konney.infrastructure.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hevlar.konney.domain.entities.IAccount;
import com.hevlar.konney.domain.valueobjects.AccountGroup;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @ManyToOne
    @JoinColumn
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "label", scope = Book.class)
    Book book;
}
