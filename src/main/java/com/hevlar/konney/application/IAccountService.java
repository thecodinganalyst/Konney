package com.hevlar.konney.application;

import com.hevlar.konney.infrastructure.entities.Account;

import java.util.List;

public interface IAccountService {
    List<Account> listAccounts(String label);
    Account getAccount(String label, String accountId);
    Account createAccount(String label, Account account) throws Exception;
    Account updateAccount(String label, String accountId, Account account);
    void deleteAccount(String label, String accountId);
}
