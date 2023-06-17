package com.hevlar.konney.application;

import com.hevlar.konney.infrastructure.entities.Journal;

import java.util.List;

public interface IJournalService {
    public List<Journal> listJournals();
    public Journal getJournal(Long journalId);
    public Journal createJournal(Journal journal);
    public Journal updateJournal(Long journalId, Journal journal);
    public void deleteJournal(Long journalId);
}
