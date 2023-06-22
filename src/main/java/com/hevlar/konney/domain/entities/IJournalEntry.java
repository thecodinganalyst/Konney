package com.hevlar.konney.domain.entities;

import com.hevlar.konney.domain.valueobjects.EntryType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public interface IJournalEntry {
    @NotNull
    Long getEntryId();
    @NotEmpty
    String getAccountId();
    @NotNull
    BigDecimal getAmount();
    @NotNull
    EntryType getEntryType();
}
