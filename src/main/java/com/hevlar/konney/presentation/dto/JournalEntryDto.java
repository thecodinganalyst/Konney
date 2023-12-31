package com.hevlar.konney.presentation.dto;

import com.hevlar.konney.domain.entities.IJournalEntry;
import com.hevlar.konney.domain.valueobjects.EntryType;
import com.hevlar.konney.infrastructure.entities.Account;
import com.hevlar.konney.infrastructure.entities.JournalEntry;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JournalEntryDto implements IJournalEntry {
    @EqualsAndHashCode.Exclude
    Long entryId;
    String accountId;
    BigDecimal amount;
    EntryType entryType;

    public static List<JournalEntryDto> fromJournalEntryList(List<JournalEntry> journalEntryList){
        return journalEntryList.stream()
                .map(JournalEntryDto::fromJournalEntry)
                .toList();
    }

    public static JournalEntryDto fromJournalEntry(JournalEntry entry){
        return JournalEntryDto.builder()
                .entryId(entry.getEntryId())
                .accountId(entry.getAccountId())
                .amount(entry.getAmount())
                .entryType(entry.getEntryType())
                .build();
    }

    public static List<JournalEntry> toJournalEntryList(List<JournalEntryDto> journalEntryDtoList){
        return journalEntryDtoList.stream()
                .map(JournalEntryDto::toJournalEntry)
                .toList();
    }

    public JournalEntry toJournalEntry(){
        return JournalEntry.builder()
                .entryId(entryId)
                .account(Account.builder().accountId(accountId).build())
                .amount(amount)
                .entryType(entryType)
                .build();
    }
}
