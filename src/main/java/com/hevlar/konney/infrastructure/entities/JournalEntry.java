package com.hevlar.konney.infrastructure.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hevlar.konney.domain.entities.IJournalEntry;
import com.hevlar.konney.domain.valueobjects.EntryType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntry implements IJournalEntry {
    @Id
    @GeneratedValue
    Long entryId;
    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn
    Account account;
    @NonNull
    BigDecimal amount;
    @NonNull
    EntryType entryType;

    @ManyToOne
    @JoinColumn
    @ToString.Exclude
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "journalId", scope = Journal.class)
    Journal journal;

    @Override
    public String getAccountId() {
        return account.getAccountId();
    }

    public String getCurrency(){
        return account.getCurrency();
    }
}
