package com.hevlar.konney.application;

import com.hevlar.konney.infrastructure.entities.Journal;

import java.util.List;

public interface IJournalService {
    List<Journal> listJournals();
    Journal getJournal(Long journalId) throws BookkeepingException;
    Journal createJournal(Journal journal);
    Journal updateJournal(Long journalId, Journal journal) throws BookkeepingException;
    void deleteJournal(Long journalId) throws BookkeepingException;
}
