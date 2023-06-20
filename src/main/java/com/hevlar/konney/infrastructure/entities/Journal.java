package com.hevlar.konney.infrastructure.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hevlar.konney.domain.entities.IJournal;
import com.hevlar.konney.domain.valueobjects.ExpenseType;
import com.hevlar.konney.domain.valueobjects.ActivityType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

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
    @ToString.Exclude
    @JoinColumn
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "label", scope = Book.class)
    Book book;
}
