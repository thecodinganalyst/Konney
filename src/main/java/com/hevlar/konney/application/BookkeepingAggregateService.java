package com.hevlar.konney.application;

import com.hevlar.konney.domain.entities.IJournalEntry;
import com.hevlar.konney.infrastructure.entities.Account;
import com.hevlar.konney.infrastructure.entities.Book;
import com.hevlar.konney.infrastructure.entities.Journal;
import com.hevlar.konney.infrastructure.entities.JournalEntry;
import com.hevlar.konney.infrastructure.repositories.AccountRepository;
import com.hevlar.konney.infrastructure.repositories.BookRepository;
import com.hevlar.konney.infrastructure.repositories.JournalEntryRepository;
import com.hevlar.konney.infrastructure.repositories.JournalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        validateBook(book);
        return bookRepository.save(book);
    }

    public Book updateBook(String label, Book book) throws BookkeepingException {
        Book savedBook = bookRepository.findById(label).orElseThrow(() -> new BookkeepingNotFoundException("Book not found"));
        if(!book.getStartDate().isEqual(savedBook.getStartDate())){
            if(accountRepository.existsByOpeningDateBeforeAndBookLabel(book.getStartDate(), book.getLabel()))
                throw new BookkeepingException("Cannot modify book start date as there are accounts with opening date before book start date");
//            if(journalRepository.existsByBookLabelAndTxDateBeforeOrPostDateBefore(book.getLabel(), book.getStartDate(), book.getStartDate()))
//                throw new BookkeepingException("Cannot modify book start date as there are journals with transaction date or post date before book start date");
        }

        if(!book.getEndDate().isEqual(savedBook.getEndDate())){
            if(accountRepository.existsByOpeningDateAfterAndBookLabel(book.getEndDate(), book.getLabel()))
                throw new BookkeepingException("Cannot modify book end date as there are accounts with opening date after book end date");
            if(journalRepository.existsByBookLabelAndTxDateAfterOrPostDateAfter(book.getLabel(), book.getEndDate(), book.getEndDate()))
                throw new BookkeepingException("Cannot modify book end date as there are journals with transaction date or post date after book end date");
        }

        validateBook(book);
        savedBook.setStartDate(book.getStartDate());
        savedBook.setEndDate(book.getEndDate());
        savedBook.setCloseUntilDate(book.getCloseUntilDate());
        return bookRepository.save(savedBook);
    }

    public void deleteBook(String label) throws BookkeepingException {
        if(!bookRepository.existsById(label)) throw new BookkeepingNotFoundException("Book not found");
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
        if(journalEntryRepository.existsByAccount_AccountId(accountId)) throw new BookkeepingException("Cannot delete account when there are journal entries present for the account");
        accountRepository.deleteById(accountId);
    }

    @Override
    public List<Journal> listJournals(String label) {
        return journalRepository.findAllByBookLabel(label);
    }

    @Override
    public Journal getJournal(String label, Long journalId) throws BookkeepingException {
        return journalRepository.findByJournalIdAndBookLabel(journalId, label).orElseThrow(() -> new BookkeepingNotFoundException("Journal not found"));
    }

    @Override
    public Journal createJournal(String label, Journal journal) throws BookkeepingException {
        Book book = bookRepository.findById(label).orElseThrow(() -> new BookkeepingNotFoundException("Book not found"));
        validateJournal(journal, book);
        journal.setBook(book);
        return journalRepository.save(journal);
    }

    @Override
    public Journal updateJournal(String label, Long journalId, Journal journal) throws BookkeepingException {
        Book book = bookRepository.findById(label).orElseThrow(() -> new BookkeepingNotFoundException("Book not found"));
        Journal savedJournal = journalRepository.findByJournalIdAndBookLabel(journalId, label).orElseThrow(() -> new BookkeepingNotFoundException("Journal not found"));
        validateJournal(journal, book);

        savedJournal.setDescription(journal.getDescription());
        savedJournal.setTxDate(journal.getTxDate());
        savedJournal.setPostDate(journal.getPostDate());
        savedJournal.getEntries().clear();

        List<JournalEntry> updatedEntries = new ArrayList<>(journal.getEntries());
        updatedEntries.forEach(je -> je.setJournal(savedJournal));
        savedJournal.getEntries().addAll(updatedEntries);

        return journalRepository.save(savedJournal);
    }

    @Override
    public void deleteJournal(String label, Long journalId) throws BookkeepingException {
        Book book = bookRepository.findById(label).orElseThrow(() -> new BookkeepingNotFoundException("Book not found"));
        Journal savedJournal = journalRepository.findByJournalIdAndBookLabel(journalId, label).orElseThrow(() -> new BookkeepingNotFoundException("Journal not found"));
        if(savedJournal.getTxDate().isBefore(book.getCloseUntilDate())) throw new BookkeepingException("Cannot delete journal for transaction date before book close date");
        if(savedJournal.getPostDate().isBefore(book.getCloseUntilDate())) throw new BookkeepingException("Cannot delete journal for post date before book close date");
        journalRepository.deleteById(journalId);
    }

    private void validateAccount(Account account, Book book) throws BookkeepingException {
        if(account.isBalanceSheetAccountGroup()){
            if(account.getOpeningDate().isBefore(book.getStartDate())) throw new BookkeepingException("Account opening date cannot be before book start date");
            if(account.getOpeningDate().isAfter(book.getEndDate())) throw new BookkeepingException("Account opening date cannot be after book end date");
        }
    }

    private void validateJournal(Journal journal, Book book) throws BookkeepingException {
        if(journal.getTxDate().isBefore(book.getStartDate())) throw new BookkeepingException("Journal transaction date cannot be before book start date");
        if(journal.getTxDate().isAfter(book.getEndDate())) throw new BookkeepingException("Journal transaction date cannot be after book end date");
        if(journal.getPostDate().isBefore(book.getStartDate())) throw new BookkeepingException("Journal post date cannot be before book start date");
        if(journal.getPostDate().isAfter(book.getEndDate())) throw new BookkeepingException("Journal post date cannot be after book end date");
        if(journal.getTxDate().isBefore(book.getCloseUntilDate())) throw new BookkeepingException("Journal transaction date is before book close date");
        if(journal.getPostDate().isBefore(book.getCloseUntilDate())) throw new BookkeepingException("Journal post date is before book close date");

        // ensure all accounts in the entries are valid
        validateJournalAccounts(journal, book);

        // ensure the debit amount and credit amount balances if they are the same currency
        validateJournalBalance(journal);
    }

    private void validateJournalAccounts(Journal journal, Book book) throws BookkeepingException {
        List<String> accountList = journal.getEntries().stream().map(JournalEntry::getAccountId).toList();

        List<String> resultAccountList = accountRepository.findAllByAccountIdInAndBookLabel(accountList, book.getLabel())
                .stream()
                .map(Account::getAccountId)
                .toList();
        List<String> unmatchedAccountList = accountList.stream()
                .filter(account -> !resultAccountList.contains(account))
                .toList();
        if(unmatchedAccountList.size() > 0){
            throw new BookkeepingException("Accounts " + unmatchedAccountList + " not found");
        }
    }

    private void validateJournalBalance(Journal journal) throws BookkeepingException {
        String currencyFirst = journal.getEntries().get(0).getCurrency();
        boolean sameCurrency = journal.getEntries().stream().allMatch(entry -> Objects.equals(entry.getCurrency(), currencyFirst));
        if(sameCurrency){
            BigDecimal debitTotal = journal.getDebitEntries()
                    .stream()
                    .map(IJournalEntry::getAmount)
                    .reduce((acc, item) -> BigDecimal.valueOf(acc.doubleValue() + item.doubleValue()))
                    .orElse(BigDecimal.ZERO);
            BigDecimal creditTotal = journal.getCreditEntries()
                    .stream()
                    .map(IJournalEntry::getAmount)
                    .reduce((acc, item) -> BigDecimal.valueOf(acc.doubleValue() + item.doubleValue()))
                    .orElse(BigDecimal.ZERO);
            if(!debitTotal.equals(creditTotal)){
                throw new BookkeepingException("Debit amount does not tally with credit amount");
            }
        }
    }

    private void validateBook(Book book) throws BookkeepingException{
        if(book.getCloseUntilDate().isBefore(book.getStartDate())) throw new BookkeepingException("Close until date cannot be before book start date");
        if(book.getCloseUntilDate().isAfter(book.getEndDate())) throw new BookkeepingException("Close until date cannot be after book end date");
    }
}
