package com.hevlar.konney.presentation.dto;

import com.hevlar.konney.domain.entities.IJournal;
import com.hevlar.konney.domain.entities.IJournalEntry;
import com.hevlar.konney.domain.valueobjects.ActivityType;
import com.hevlar.konney.domain.valueobjects.EntryType;
import com.hevlar.konney.domain.valueobjects.ExpenseType;
import com.hevlar.konney.infrastructure.entities.Journal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JournalDto implements IJournal {
    Long journalId;
    LocalDate txDate;
    LocalDate postDate;
    String description;
    List<JournalEntryDto> entries;
    ExpenseType expenseType;
    ActivityType activityType;

    public Journal toJournal(){
        return Journal.builder()
                .txDate(txDate)
                .postDate(postDate)
                .description(description)
                .entries(JournalEntryDto.toJournalEntryList(entries))
                .build();
    }

    @Override
    public List<? extends IJournalEntry> getDebitEntries() {
        return entries.stream().filter(entry -> entry.getEntryType() == EntryType.Debit).toList();
    }

    @Override
    public List<? extends IJournalEntry> getCreditEntries() {
        return entries.stream().filter(entry -> entry.getEntryType() == EntryType.Credit).toList();
    }
}
