package com.hevlar.konney.infrastructure.repositories;

import com.hevlar.konney.infrastructure.entities.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
}
