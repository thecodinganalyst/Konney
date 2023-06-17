package com.hevlar.konney.infrastructure.entities;

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
    Book book;
}
