package com.hevlar.konney.application;

import com.hevlar.konney.infrastructure.entities.Account;
import com.hevlar.konney.infrastructure.entities.Book;
import com.hevlar.konney.infrastructure.entities.Journal;
import com.hevlar.konney.infrastructure.repositories.AccountRepository;
import com.hevlar.konney.infrastructure.repositories.BookRepository;
import com.hevlar.konney.infrastructure.repositories.JournalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookkeepingAggregateService implements IBookService, IAccountService, IJournalService {
    private final BookRepository bookRepository;
    private final AccountRepository accountRepository;
    private final JournalRepository journalRepository;

    public BookkeepingAggregateService(BookRepository bookRepository, AccountRepository accountRepository, JournalRepository journalRepository){
        this.bookRepository = bookRepository;
        this.accountRepository = accountRepository;
        this.journalRepository = journalRepository;
    }

    public List<Book> listBooks(){
        return bookRepository.findAll();
    }

    public Book getBook(String label){
        return bookRepository.findById(label).orElseThrow(NoSuchElementException::new);
    }

    public Book createBook(Book book){
        return bookRepository.save(book);
    }

    public Book updateBook(String label, Book book) throws Exception {
        if(!bookRepository.existsById(label)) throw new NoSuchElementException();
        if(accountRepository.existsByBookLabel(label)) throw new Exception("Cannot update book when accounts exists");

        book.setLabel(label);
        return bookRepository.save(book);
    }

    public void deleteBook(String label) throws Exception {
        if(bookRepository.existsById(label)) throw new NoSuchElementException();
        if(accountRepository.existsByBookLabel(label)) throw new Exception("Cannot delete book when accounts exists");
        bookRepository.deleteById(label);
    }

    @Override
    public List<Account> listAccounts(String label) {
        return accountRepository.findAllByBookLabel(label);
    }

    @Override
    public Account getAccount(String label, String accountId) {
        return accountRepository.findByAccountIdAndBookLabel(accountId, label).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Account createAccount(String label, Account account) throws Exception {
        Book book = bookRepository.findById(label).orElseThrow(() -> new Exception("Book label not found"));
        return accountRepository.save(account);
    }

    @Override
    public Account updateAccount(String label, String accountId, Account account) {
        if(accountRepository.findByAccountIdAndBookLabel(accountId, label).isEmpty()) throw new NoSuchElementException();
        account.setAccountId(accountId);
        return accountRepository.save(account);
    }

    @Override
    public void deleteAccount(String label, String accountId) {
        if(accountRepository.findByAccountIdAndBookLabel(accountId, label).isEmpty()) throw new NoSuchElementException();
        accountRepository.deleteById(accountId);
    }

    @Override
    public List<Journal> listJournals() {
        return journalRepository.findAll();
    }

    @Override
    public Journal getJournal(Long journalId) {
        return journalRepository.findById(journalId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Journal createJournal(Journal journal) {
        return journalRepository.save(journal);
    }

    @Override
    public Journal updateJournal(Long journalId, Journal journal) {
        if(journalRepository.existsById(journalId)) throw new NoSuchElementException();
        journal.setJournalId(journalId);
        return journalRepository.save(journal);
    }

    @Override
    public void deleteJournal(Long journalId) {
        if(journalRepository.existsById(journalId)) throw new NoSuchElementException();
        journalRepository.deleteById(journalId);
    }
}
