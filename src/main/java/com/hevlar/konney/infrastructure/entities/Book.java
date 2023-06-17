package com.hevlar.konney.infrastructure.entities;

import com.hevlar.konney.domain.entities.IBook;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Book implements IBook {
    @Id
    String label;
    LocalDate startDate;
    LocalDate endDate;
    @OneToMany(mappedBy = "book")
    List<Account> accountList;
    @OneToMany(mappedBy = "book")
    List<Journal> journalList;

}
