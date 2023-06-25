package com.hevlar.konney.domain.entities;

import com.hevlar.konney.domain.valueobjects.AccountGroup;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IBalanceSheet {
    LocalDate getBalanceDate();
    String getCurrency();
    List<IBalanceSheetAccountGroup> getAccountGroupList();
    default List<IBalanceSheetAccountGroup> getFixedAssetGroup(){
        return getAccountGroupList().stream().filter(group -> group.getAccountGroup() == AccountGroup.FixedAsset).toList();
    }

    default List<IBalanceSheetAccountGroup> getCurrentAssetGroup(){
        return getAccountGroupList().stream().filter(group -> group.getAccountGroup() == AccountGroup.CurrentAsset).toList();
    }

    default List<IBalanceSheetAccountGroup> getLongTermLiabilityGroup(){
        return getAccountGroupList().stream().filter(group -> group.getAccountGroup() == AccountGroup.LongTermLiability).toList();
    }

    default List<IBalanceSheetAccountGroup> getCurrentLiabilityGroup(){
        return getAccountGroupList().stream().filter(group -> group.getAccountGroup() == AccountGroup.CurrentLiability).toList();
    }

    default List<IBalanceSheetAccountGroup> getEquityGroup(){
        return getAccountGroupList().stream().filter(group -> group.getAccountGroup() == AccountGroup.Equity).toList();
    }

    default List<IBalanceSheetAccountGroup> getDebitGroups(){
        return getAccountGroupList().stream()
                .filter(group -> group.getAccountGroup() == AccountGroup.FixedAsset || group.getAccountGroup() == AccountGroup.CurrentAsset)
                .toList();
    }

    default List<IBalanceSheetAccountGroup> getCreditGroups(){
        return getAccountGroupList().stream()
                .filter(group -> group.getAccountGroup() == AccountGroup.LongTermLiability || group.getAccountGroup() == AccountGroup.CurrentLiability || group.getAccountGroup() == AccountGroup.Equity)
                .toList();
    }

    default BigDecimal getDebitBalance(){
        return getDebitGroups().stream()
                .map(IBalanceSheetAccountGroup::getAccountGroupTotal)
                .reduce((acc, item) -> BigDecimal.valueOf(acc.doubleValue() + item.doubleValue()))
                .orElse(BigDecimal.ZERO);
    }

    default BigDecimal getCreditBalance(){
        return getCreditGroups().stream()
                .map(IBalanceSheetAccountGroup::getAccountGroupTotal)
                .reduce((acc, item) -> BigDecimal.valueOf(acc.doubleValue() + item.doubleValue()))
                .orElse(BigDecimal.ZERO);
    }
}
