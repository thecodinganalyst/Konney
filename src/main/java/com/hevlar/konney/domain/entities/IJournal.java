package com.hevlar.konney.domain.entities;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public interface IJournal {
    @NotNull
    Long getJournalId();
    @NotNull
    LocalDate getTxDate();
    @NotNull
    LocalDate getPostDate();
    @NotEmpty
    String getDescription();
    List<? extends IJournalEntry> getDebitEntries();
    List<? extends IJournalEntry> getCreditEntries();
    List<? extends IJournalEntry> getEntries();
}
