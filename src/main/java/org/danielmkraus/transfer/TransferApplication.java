package org.danielmkraus.transfer;

import org.danielmkraus.transfer.controller.AccountController;
import org.danielmkraus.transfer.controller.TransferController;
import org.danielmkraus.transfer.repository.AccountRepository;
import org.danielmkraus.transfer.service.AccountLockService;
import org.danielmkraus.transfer.service.AccountService;
import org.danielmkraus.transfer.service.TransferService;

import javax.ws.rs.core.Application;
import java.util.Set;

import static org.danielmkraus.transfer.ServerProperties.ACCOUNT_LOCK_TIMEOUT_IN_MILLISECONDS;
import static org.danielmkraus.transfer.ServerProperties.intProperty;

public class TransferApplication extends Application {

    @Override
    public Set<Object> getSingletons() {
        AccountRepository repository = new AccountRepository();
        AccountLockService lockService = new AccountLockService(intProperty(ACCOUNT_LOCK_TIMEOUT_IN_MILLISECONDS));
        return Set.of(
                createAccountController(repository, lockService),
                createTransferController(repository, lockService));
    }

    private AccountController createAccountController(AccountRepository repository, AccountLockService lockService) {
        return new AccountController(
                new AccountService(repository, lockService));
    }

    private TransferController createTransferController(AccountRepository repository, AccountLockService lockService) {
        return new TransferController(
                new TransferService(repository, lockService));
    }
}
