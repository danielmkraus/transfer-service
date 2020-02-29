package org.danielmkraus.transfer.service;

import org.danielmkraus.transfer.domain.TransferRequest;
import org.danielmkraus.transfer.exception.AccountLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AccountLockService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountLockService.class);
    private final Map<String, ReentrantReadWriteLock> locks;
    private final long lockTimeoutMilliseconds;

    public AccountLockService(long lockTimeoutMilliseconds) {
        this.lockTimeoutMilliseconds = lockTimeoutMilliseconds;
        locks = Collections.synchronizedMap(new WeakHashMap<>());
    }

    void lockForWrite(String accountId, Runnable runnable) {
        tryLock(ReadWriteLock::writeLock, accountId, supplierAdapter(runnable));
    }

    <X> X lockForRead(String accountId, Supplier<X> supplier) {
        return tryLock(ReadWriteLock::readLock, accountId, supplier);
    }

    private <X> X tryLock(Function<ReadWriteLock, Lock> lockFunction,
                          String accountId,
                          Supplier<X> supplier) {
        LOG.debug("Trying to lockForRead account {}", accountId);
        var lock = locks.computeIfAbsent(accountId, this::createLock);
        try {
            var lockResource = lockFunction.apply(lock);
            var lockAcquired = lockResource.tryLock(lockTimeoutMilliseconds, MILLISECONDS);
            if (!lockAcquired) {
                LOG.debug("failed to lockForRead account {} for {}", accountId, lockResource);
                throw new AccountLockException();
            }
            try {
                LOG.debug("locked account {} for {}", accountId, lockResource);
                return supplier.get();
            } finally {
                LOG.debug("unlocked account {} for {}", accountId, lockResource);
                lockResource.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AccountLockException(e);
        }
    }

    private ReentrantReadWriteLock createLock(String accountId) {
        LOG.debug("Creating a new Lock object for account id {}", accountId);
        return new ReentrantReadWriteLock();
    }

    void lockForWrite(TransferRequest transferRequest, Runnable consumer) {
        lockForWrite(transferRequest.getFromAccountId(),
                () -> lockForWrite(transferRequest.getToAccountId(), consumer)
        );
    }

    private Supplier<Void> supplierAdapter(Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }
}
