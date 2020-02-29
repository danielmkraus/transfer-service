package org.danielmkraus.transfer.service;

import org.danielmkraus.transfer.domain.Account;
import org.danielmkraus.transfer.exception.AccountAlreadyRegisteredException;
import org.danielmkraus.transfer.repository.AccountRepository;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

public class AccountService {
    private final AccountRepository repository;
    private final AccountLockService lockService;

    public AccountService(AccountRepository repository, AccountLockService lockService) {
        this.repository = repository;
        this.lockService = lockService;
    }

    public Account get(String accountId) {
        var lock = lockService.lockForRead(accountId);
        try {
            return repository.getById(accountId);
        } finally {
            lock.unlock();
        }
    }

    public void register(String accountId) {
        if(repository.findById(accountId).isPresent()){
            throw new AccountAlreadyRegisteredException();
        }
        set(accountId, ZERO);
    }

    public void set(String accountId, BigDecimal balance) {
        var lock = lockService.lockForWrite(accountId);
        try {
            repository.save(new Account(accountId, balance));
        } finally {
            lock.unlock();
        }
    }
}
