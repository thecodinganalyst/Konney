package com.hevlar.konney.domain.entities;

import com.hevlar.konney.domain.valueobjects.ActivityType;
import com.hevlar.konney.domain.valueobjects.ExpenseType;
import com.hevlar.konney.infrastructure.entities.Book;

import java.time.LocalDate;
import java.util.List;

public interface IJournal {
    Long getJournalId();
    LocalDate getTxDate();
    LocalDate getPostDate();
    String getDescription();
    List<? extends IJournalEntry> getDebitEntries();
    List<? extends IJournalEntry> getCreditEntries();
    ExpenseType getExpenseType();
    ActivityType getActivityType();
    Book getBook();
}
