package com.hevlar.konney.infrastructure.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hevlar.konney.domain.entities.IJournal;
import com.hevlar.konney.domain.entities.IJournalEntry;
import com.hevlar.konney.domain.valueobjects.EntryType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Journal implements IJournal {
    @Id
    @GeneratedValue
    Long journalId;
    LocalDate txDate;
    LocalDate postDate;
    String description;

    @Builder.Default
    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<JournalEntry> entries = new ArrayList<>();

    @ManyToOne
    @ToString.Exclude
    @JoinColumn
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "label", scope = Book.class)
    Book book;

    public List<? extends IJournalEntry> getDebitEntries() {
        return getEntries().stream().filter(entry -> entry.getEntryType() == EntryType.Debit).toList();
    }

    public List<? extends IJournalEntry> getCreditEntries() {
        return getEntries().stream().filter(entry -> entry.getEntryType() == EntryType.Credit).toList();
    }
}
