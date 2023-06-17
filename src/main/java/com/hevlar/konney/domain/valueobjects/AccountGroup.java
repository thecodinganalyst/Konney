package com.hevlar.konney.domain.valueobjects;

import java.util.Enumeration;
import java.util.List;

import static com.hevlar.konney.domain.valueobjects.EntryType.Credit;
import static com.hevlar.konney.domain.valueobjects.EntryType.Debit;

public enum AccountGroup {
    FixedAsset(Debit),
    CurrentAsset(Debit),
    CurrentLiability(Credit),
    LongTermLiability(Credit),
    Revenue(Credit),
    Expense(Debit),
    Gain(Credit),
    Loss(Debit),
    Equity(Credit);
    public final EntryType entryType;

    AccountGroup(EntryType entryType){
        this.entryType = entryType;
    }

    public static final List<AccountGroup> balanceSheetAccountGroupList = List.of(FixedAsset, CurrentAsset, CurrentLiability, LongTermLiability, Equity);

    public static final List<AccountGroup> incomeStatementAccountGroupList = List.of(Revenue, Expense, Gain, Loss);
}
