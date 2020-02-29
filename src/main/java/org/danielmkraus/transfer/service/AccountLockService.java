package org.danielmkraus.transfer.service;

import org.danielmkraus.transfer.domain.TransferRequest;
import org.danielmkraus.transfer.exception.AccountLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AccountLockService {
    private static final Logger LOG = LoggerFactory.getLogger(AccountLockService.class);
    private final Map<String, ReentrantReadWriteLock> locks;
    private final long lockTimeoutMilliseconds;

    public AccountLockService(long lockTimeoutMilliseconds) {
        this.lockTimeoutMilliseconds = lockTimeoutMilliseconds;
        locks = Collections.synchronizedMap(new WeakHashMap<>());
    }

    AccountLock lockForWrite(TransferRequest transferRequest){
        var fromAccountLock = tryLock(ReadWriteLock::writeLock, transferRequest.getFromAccountId());
        try {
            var toAccountLock = tryLock(ReadWriteLock::writeLock, transferRequest.getToAccountId());
            return new AccountLock(fromAccountLock, toAccountLock);
        } catch (AccountLockException e){
            fromAccountLock.unlock();
            throw new AccountLockException(e);
        }
    }

    AccountLock lockForWrite(String accountId){
        return new AccountLock(tryLock(ReadWriteLock::writeLock, accountId));
    }

    AccountLock lockForRead(String accountId){
        return new AccountLock(tryLock(ReadWriteLock::readLock, accountId));
    }

    private Lock tryLock(Function<ReadWriteLock, Lock> lockFunction, String accountId) {
        LOG.debug("Trying to lockForRead account {}", accountId);
        var lock = getAccountLock(accountId);
        try {
            var lockResource = lockFunction.apply(lock);
            var lockAcquired = lockResource.tryLock(lockTimeoutMilliseconds, MILLISECONDS);
            if(lockAcquired){
                LOG.debug("locked account {} for {}", accountId, lockResource);
                return lockResource;
            }
            LOG.debug("failed to lockForRead account {} for {}", accountId, lockResource);
            throw new AccountLockException();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AccountLockException(e);
        }
    }

    private ReentrantReadWriteLock getAccountLock(String accountId){
        return locks.computeIfAbsent(accountId, this::createLock);
    }

    private ReentrantReadWriteLock createLock(String accountId) {
        LOG.debug("Creating a new Lock object for account id {}", accountId);
        return new ReentrantReadWriteLock();
    }

    static class AccountLock {
        private final Stream<Lock> locks;

        private AccountLock(Lock... locks){
            this.locks = Stream.of(locks);
        }

        void unlock(){
            LOG.debug("Unlocking {}", locks);
            locks.forEach(Lock::unlock);
        }
    }

}
