package com.hevlar.konney.infrastructure.entities;

import com.hevlar.konney.domain.entities.IJournalEntry;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class JournalEntry implements IJournalEntry {
    @Id
    @GeneratedValue
    Long entryId;
    @OneToOne(optional = false)
    Account account;
    String currency;
    BigDecimal amount;
    @ManyToOne
    @JoinColumn
    Journal journal;
}
