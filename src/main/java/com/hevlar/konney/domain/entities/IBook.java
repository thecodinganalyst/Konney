package com.hevlar.konney.domain.entities;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public interface IBook {
    @NotNull
    String getLabel();
    @NotNull
    LocalDate getStartDate();
    @NotNull
    LocalDate getEndDate();
    @NotNull
    LocalDate getCloseUntilDate();
    List<? extends IAccount> getAccountList();
    List<? extends IJournal> getJournalList();
}
