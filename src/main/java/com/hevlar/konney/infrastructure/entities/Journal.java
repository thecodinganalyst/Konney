package com.hevlar.konney.infrastructure.entities;

import com.hevlar.konney.domain.entities.IJournal;
import com.hevlar.konney.domain.valueobjects.ExpenseType;
import com.hevlar.konney.domain.valueobjects.ActivityType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Journal implements IJournal {
    @Id
    @GeneratedValue
    Long journalId;
    LocalDate txDate;
    LocalDate postDate;
    String description;
    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<JournalEntry> debitEntries;
    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<JournalEntry> creditEntries;
    ExpenseType expenseType;
    ActivityType activityType;
    @ManyToOne
    @JoinColumn
    Book book;
}
