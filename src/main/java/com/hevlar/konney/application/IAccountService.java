package com.hevlar.konney.application;

import com.hevlar.konney.infrastructure.entities.Account;

import java.util.List;

public interface IAccountService {
    List<Account> listAccounts(String label);
    Account getAccount(String label, String accountId) throws BookkeepingNotFoundException;
    Account createAccount(String label, Account account) throws BookkeepingException;
    Account updateAccount(String label, String accountId, Account account) throws BookkeepingException;
    void deleteAccount(String label, String accountId) throws BookkeepingException;
}
