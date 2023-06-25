package com.hevlar.konney.domain.entities;

import com.hevlar.konney.domain.valueobjects.AccountGroup;

import java.math.BigDecimal;
import java.util.List;

public interface IBalanceSheetAccountGroup {
    AccountGroup getAccountGroup();
    List<IBalanceSheetAccount> getAccounts();

    default BigDecimal getAccountGroupTotal(){
        return getAccounts().stream()
                .map(IBalanceSheetAccount::getBalanceAmount)
                .reduce((acc, item) -> BigDecimal.valueOf(acc.doubleValue() + item.doubleValue()))
                .orElse(BigDecimal.ZERO);
    }
}
