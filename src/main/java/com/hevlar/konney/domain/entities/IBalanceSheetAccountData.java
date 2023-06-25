package com.hevlar.konney.domain.entities;

import com.hevlar.konney.domain.valueobjects.EntryType;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface IBalanceSheetAccountData {
    LocalDate getTxDate();
    LocalDate getPostDate();
    String getDescription();
    BigDecimal getAmount();
    EntryType getEntryType();
    Long getEntryId();
    Long getJournalId();
}
