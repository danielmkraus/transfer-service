package org.danielmkraus.transfer.service;

import org.danielmkraus.transfer.domain.Account;
import org.danielmkraus.transfer.domain.TransferRequest;
import org.danielmkraus.transfer.exception.InsufficientFundsException;
import org.danielmkraus.transfer.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class TransferService {
    private static final Logger LOG = LoggerFactory.getLogger(TransferService.class);
    private final AccountRepository repository;
    private final AccountLockService lockService;

    public TransferService(AccountRepository repository, AccountLockService lockService) {
        this.repository = repository;
        this.lockService = lockService;
    }

    public void transfer(TransferRequest transferRequest) {
        LOG.debug("Processing transfer {}", transferRequest);
        transferRequest.validate();
        lockService.lockForWrite(transferRequest, () -> {
            var from = repository.getById(transferRequest.getFromAccountId());
            var to = repository.getById(transferRequest.getToAccountId());
            transfer(transferRequest.getAmount(), from, to);
        });
    }

    private void transfer(BigDecimal transferAmount, Account from, Account to) {
        validateTransferAmountAvailable(transferAmount, from);
        from.subtractBalance(transferAmount);
        to.addBalance(transferAmount);
        repository.save(from);
        repository.save(to);
    }

    private void validateTransferAmountAvailable(BigDecimal transferAmount, Account account) {
        if (account.isBalanceLessThan(transferAmount)) {
            throw new InsufficientFundsException();
        }
    }
}
