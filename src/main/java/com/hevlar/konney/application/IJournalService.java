package com.hevlar.konney.application;

import com.hevlar.konney.infrastructure.entities.Journal;

import java.util.List;

public interface IJournalService {
    List<Journal> listJournals(String label);
    Journal getJournal(String label, Long journalId) throws BookkeepingNotFoundException;
    Journal createJournal(String label, Journal journal) throws BookkeepingException;
    Journal updateJournal(String label, Long journalId, Journal journal) throws BookkeepingException;
    void deleteJournal(String label, Long journalId) throws BookkeepingException;
}
