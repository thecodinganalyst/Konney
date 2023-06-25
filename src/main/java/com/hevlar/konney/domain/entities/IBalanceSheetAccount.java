package com.hevlar.konney.domain.entities;

import com.hevlar.konney.domain.valueobjects.EntryType;
import com.hevlar.konney.infrastructure.entities.Account;

import java.math.BigDecimal;
import java.util.List;

public interface IBalanceSheetAccount {
    Account getAccount();
    List<IBalanceSheetAccountData> getData();
    default List<IBalanceSheetAccountData> getDebitData(){
        return getData().stream().filter(data -> data.getEntryType() == EntryType.Debit).toList();
    }
    default List<IBalanceSheetAccountData> getCreditData(){
        return getData().stream().filter(data -> data.getEntryType() == EntryType.Credit).toList();
    }

    default BigDecimal getTotalDebitAmount(){
        return getDebitData().stream()
                .map(IBalanceSheetAccountData::getAmount)
                .reduce((acc, item) -> BigDecimal.valueOf(acc.doubleValue() + item.doubleValue()))
                .orElse(BigDecimal.ZERO);
    }

    default BigDecimal getTotalCreditAmount(){
        return getCreditData().stream()
                .map(IBalanceSheetAccountData::getAmount)
                .reduce((acc, item) -> BigDecimal.valueOf(acc.doubleValue() + item.doubleValue()))
                .orElse(BigDecimal.ZERO);
    }

    default BigDecimal getBalanceAmount(){
        if(getAccount().getAccountGroup().entryType == EntryType.Debit){
            return BigDecimal.valueOf(getAccount().getOpeningBalance().doubleValue() + getTotalDebitAmount().doubleValue() - getTotalCreditAmount().doubleValue());
        }else{
            return BigDecimal.valueOf(getAccount().getOpeningBalance().doubleValue() + getTotalCreditAmount().doubleValue() - getTotalDebitAmount().doubleValue());
        }
    }
}
