package com.hevlar.konney.infrastructure.repositories;

import com.hevlar.konney.infrastructure.entities.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    boolean existsByAccount_AccountId(String accountId);
}
