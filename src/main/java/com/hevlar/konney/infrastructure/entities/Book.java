package com.hevlar.konney.infrastructure.entities;

import com.hevlar.konney.domain.entities.IBook;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book implements IBook{
    @Id
    String label;
    LocalDate startDate;
    LocalDate endDate;
    @Builder.Default
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    List<Account> accountList = new ArrayList<>();
    @Builder.Default
    @OneToMany(mappedBy = "book")
    List<Journal> journalList = new ArrayList<>();
}
