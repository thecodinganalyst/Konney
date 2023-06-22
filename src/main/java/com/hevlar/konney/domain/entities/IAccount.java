package com.hevlar.konney.domain.entities;

import com.hevlar.konney.domain.validation.ValidAccount;
import com.hevlar.konney.domain.valueobjects.AccountGroup;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@ValidAccount
public interface IAccount {
    @NotEmpty
    String getAccountId();
    @NotEmpty
    String getAccountName();
    @NotNull
    AccountGroup getAccountGroup();
    LocalDate getOpeningDate();
    String getCurrency();
    BigDecimal getOpeningBalance();

    default boolean isBalanceSheetAccountGroup() {
        return AccountGroup.balanceSheetAccountGroupList.contains(getAccountGroup());
    }

    default boolean isIncomeStatementAccountGroup(){
        return AccountGroup.incomeStatementAccountGroupList.contains(getAccountGroup());
    }
}
