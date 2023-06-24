package com.hevlar.konney.infrastructure.repositories;

import com.hevlar.konney.infrastructure.entities.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findAllByBookLabel(String label);
    Optional<Journal> findByJournalIdAndBookLabel(Long journalId, String label);
    boolean existsByBookLabelAndTxDateBeforeOrPostDateBefore(String label, LocalDate txDate, LocalDate postDate);
    boolean existsByBookLabelAndTxDateAfterOrPostDateAfter(String label, LocalDate txDate, LocalDate postDate);
}
