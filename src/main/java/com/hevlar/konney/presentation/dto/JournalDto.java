package com.hevlar.konney.presentation.dto;

import com.hevlar.konney.domain.entities.IJournal;
import com.hevlar.konney.infrastructure.entities.Journal;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JournalDto implements IJournal {
    @EqualsAndHashCode.Exclude
    Long journalId;
    LocalDate txDate;
    LocalDate postDate;
    String description;
    List<JournalEntryDto> entries;

    public static List<JournalDto> fromJournalList(List<Journal> journalList){
        return journalList.stream().map(JournalDto::fromJournal).toList();
    }

    public static JournalDto fromJournal(Journal journal){
        return JournalDto.builder()
                .journalId(journal.getJournalId())
                .txDate(journal.getTxDate())
                .description(journal.getDescription())
                .postDate(journal.getPostDate())
                .entries(JournalEntryDto.fromJournalEntryList(journal.getEntries()))
                .build();
    }

    public Journal toJournal(){
        Journal journal = Journal.builder()
                .txDate(txDate)
                .postDate(postDate)
                .description(description)
                .entries(JournalEntryDto.toJournalEntryList(entries))
                .build();
        journal.setEntries(
                journal.getEntries().stream().peek(je -> je.setJournal(journal)).toList()
        );
        return journal;
    }
}
