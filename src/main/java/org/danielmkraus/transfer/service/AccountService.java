package org.danielmkraus.transfer.service;

import org.danielmkraus.transfer.domain.Account;
import org.danielmkraus.transfer.exception.AccountAlreadyRegisteredException;
import org.danielmkraus.transfer.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static org.danielmkraus.transfer.domain.Account.newAccount;

public class AccountService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository repository;
    private final AccountLockService lockService;

    public AccountService(AccountRepository repository, AccountLockService lockService) {
        this.repository = repository;
        this.lockService = lockService;
    }

    public void register(String accountId) {
        if (repository.findById(accountId).isPresent()) {
            throw new AccountAlreadyRegisteredException();
        }
        set(accountId, ZERO);
    }

    public Account get(String accountId) {
        return lockService.lockForRead(accountId,
                () -> repository.getById(accountId));
    }

    public void set(String accountId, BigDecimal balance) {
        Account account = newAccount(accountId, balance);
        LOG.debug("Saving account {}", account);
        lockService.lockForWrite(accountId,
                () -> repository.save(account));
    }
}
