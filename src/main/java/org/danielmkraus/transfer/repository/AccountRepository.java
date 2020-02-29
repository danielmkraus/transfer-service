package org.danielmkraus.transfer.repository;

import org.danielmkraus.transfer.domain.Account;
import org.danielmkraus.transfer.exception.AccountNotFoundException;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;

public class AccountRepository {

    private final ConcurrentHashMap<String, Account> accounts;

    public AccountRepository() {
        accounts = new ConcurrentHashMap<>();
    }

    public Optional<Account> findById(String accountId) {
        return ofNullable(accounts.get(accountId));
    }

    public Account getById(String id) {
        return findById(id)
                .orElseThrow(AccountNotFoundException::new);
    }

    public void save(Account account) {
        accounts.put(account.getId(), account);
    }
}
