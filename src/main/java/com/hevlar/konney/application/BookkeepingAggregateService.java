package com.hevlar.konney.application;

import com.hevlar.konney.infrastructure.entities.Account;
import com.hevlar.konney.infrastructure.entities.Book;
import com.hevlar.konney.infrastructure.entities.Journal;
import com.hevlar.konney.infrastructure.repositories.AccountRepository;
import com.hevlar.konney.infrastructure.repositories.BookRepository;
import com.hevlar.konney.infrastructure.repositories.JournalEntryRepository;
import com.hevlar.konney.infrastructure.repositories.JournalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookkeepingAggregateService implements IBookService, IAccountService, IJournalService {
    private final BookRepository bookRepository;
    private final AccountRepository accountRepository;
    private final JournalRepository journalRepository;
    private final JournalEntryRepository journalEntryRepository;

    public BookkeepingAggregateService(
            BookRepository bookRepository,
            AccountRepository accountRepository,
            JournalRepository journalRepository,
            JournalEntryRepository journalEntryRepository){
        this.bookRepository = bookRepository;
        this.accountRepository = accountRepository;
        this.journalRepository = journalRepository;
        this.journalEntryRepository = journalEntryRepository;
    }

    public List<Book> listBooks(){
        return bookRepository.findAll();
    }

    public Book getBook(String label) throws BookkeepingException {
        return bookRepository.findById(label).orElseThrow(() -> new BookkeepingNotFoundException("Book not found"));
    }

    public Book createBook(Book book) throws BookkeepingException {
        if(bookRepository.existsById(book.getLabel())) throw new BookkeepingException("Book with the same label already exists");
        return bookRepository.save(book);
    }

    public Book updateBook(String label, Book book) throws BookkeepingException {
        Book savedBook = bookRepository.findById(label).orElseThrow(() -> new BookkeepingNotFoundException("Book not found"));
        if(accountRepository.existsByBookLabel(label)) throw new BookkeepingException("Cannot update book when accounts exists");
        savedBook.setStartDate(book.getStartDate());
        savedBook.setEndDate(book.getEndDate());
        return bookRepository.save(savedBook);
    }

    public void deleteBook(String label) throws BookkeepingException {
        if(bookRepository.existsById(label)) throw new BookkeepingNotFoundException("Book not found");
        if(accountRepository.existsByBookLabel(label)) throw new BookkeepingException("Cannot delete book when accounts exists");
        bookRepository.deleteById(label);
    }

    @Override
    public List<Account> listAccounts(String label) {
        return accountRepository.findAllByBookLabel(label);
    }

    @Override
    public Account getAccount(String label, String accountId) throws BookkeepingException {
        return accountRepository.findByAccountIdAndBookLabel(accountId, label).orElseThrow(() -> new BookkeepingNotFoundException("Account not found"));
    }

    @Override
    public Account createAccount(String label, Account account) throws BookkeepingException {
        Book book = bookRepository.findById(label).orElseThrow(() -> new BookkeepingNotFoundException("Book not found"));
        validateAccount(account, book);
        account.setBook(book);
        return accountRepository.save(account);
    }

    @Override
    public Account updateAccount(String label, String accountId, Account account) throws BookkeepingException {
        Book book = bookRepository.findById(label).orElseThrow(() -> new BookkeepingNotFoundException("Book not found"));
        validateAccount(account, book);

        Account savedAccount = accountRepository.findByAccountIdAndBookLabel(accountId, label).orElseThrow(() -> new BookkeepingNotFoundException("Account not found"));

        if(account.getAccountGroup() != savedAccount.getAccountGroup()){
            if(journalEntryRepository.existsByAccount_AccountId(account.getAccountId()))
                throw new BookkeepingException("Cannot modify account group when there are journal entries present for the account");
        }

        savedAccount.setAccountName(account.getAccountName());
        savedAccount.setAccountGroup(account.getAccountGroup());
        if(account.isBalanceSheetAccountGroup()){
            savedAccount.setOpeningDate(account.getOpeningDate());
            savedAccount.setCurrency(account.getCurrency());
            savedAccount.setOpeningBalance(account.getOpeningBalance());
        }else{
            savedAccount.setOpeningDate(null);
            savedAccount.setCurrency(null);
            savedAccount.setOpeningBalance(null);
        }
        return accountRepository.save(savedAccount);
    }

    @Override
    public void deleteAccount(String label, String accountId) throws BookkeepingException {
        if(accountRepository.findByAccountIdAndBookLabel(accountId, label).isEmpty()) throw new BookkeepingNotFoundException("Account not found");
        accountRepository.deleteById(accountId);
    }

    @Override
    public List<Journal> listJournals() {
        return journalRepository.findAll();
    }

    @Override
    public Journal getJournal(Long journalId) throws BookkeepingException {
        return journalRepository.findById(journalId).orElseThrow(() -> new BookkeepingNotFoundException("Journal not found"));
    }

    @Override
    public Journal createJournal(Journal journal) {
        return journalRepository.save(journal);
    }

    @Override
    public Journal updateJournal(Long journalId, Journal journal) throws BookkeepingException {
        if(journalRepository.existsById(journalId)) throw new BookkeepingNotFoundException("Journal not found");
        journal.setJournalId(journalId);
        return journalRepository.save(journal);
    }

    @Override
    public void deleteJournal(Long journalId) throws BookkeepingException {
        if(journalRepository.existsById(journalId)) throw new BookkeepingNotFoundException("Journal not found");
        journalRepository.deleteById(journalId);
    }

    private void validateAccount(Account account, Book book) throws BookkeepingException {
        if(account.isBalanceSheetAccountGroup()){
            if(account.getOpeningDate().isBefore(book.getStartDate())) throw new BookkeepingException("Account opening date cannot be before book start date");
            if(account.getOpeningDate().isAfter(book.getEndDate())) throw new BookkeepingException("Account opening date cannot be after book end date");
        }
    }
}
