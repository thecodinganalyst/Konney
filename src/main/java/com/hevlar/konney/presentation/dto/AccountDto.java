package com.hevlar.konney.presentation.dto;

import com.hevlar.konney.domain.entities.IAccount;
import com.hevlar.konney.domain.entities.IBook;
import com.hevlar.konney.domain.valueobjects.AccountGroup;
import com.hevlar.konney.infrastructure.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountDto implements IAccount {
    String accountId;
    String accountName;
    AccountGroup accountGroup;
    LocalDate openingDate;
    String currency;
    BigDecimal openingBalance;

    public static List<AccountDto> fromAccountList(List<Account> accountList){
        return accountList.stream()
                .map(AccountDto::fromAccount)
                .toList();
    }

    public static AccountDto fromAccount(Account account){
        return AccountDto.builder()
                .accountId(account.getAccountId())
                .accountName(account.getAccountName())
                .accountGroup(account.getAccountGroup())
                .openingDate(account.getOpeningDate())
                .currency(account.getCurrency())
                .openingBalance(account.getOpeningBalance())
                .build();
    }

    public Account toAccount(){
        return Account.builder()
                .accountId(accountId)
                .accountName(accountName)
                .accountGroup(accountGroup)
                .openingDate(openingDate)
                .currency(currency)
                .openingBalance(openingBalance)
                .build();
    }

    @Override
    public IBook getBook() {
        return null;
    }
}
