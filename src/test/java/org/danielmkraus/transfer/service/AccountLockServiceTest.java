package org.danielmkraus.transfer.service;

import org.danielmkraus.transfer.domain.TransferRequest;
import org.danielmkraus.transfer.exception.AccountLockException;
import org.danielmkraus.transfer.service.AccountLockService.AccountLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.danielmkraus.transfer.TransferTests.*;

@Tag(UNIT_TEST)
class AccountLockServiceTest {
    private static final TransferRequest SAMPLE_TRANSFER_REQUEST =
            TransferRequest.aTransfer()
                    .from(AN_ACCOUNT_ID)
                    .to(ANOTHER_ACCOUNT_ID)
                    .ofAmount(TEN);

    private ExecutorService firstThreadExecutor;
    private ExecutorService secondThreadExecutor;

    private AccountLockService accountLockService;

    @BeforeEach
    void setup() {
        firstThreadExecutor = Executors.newSingleThreadExecutor();
        secondThreadExecutor = Executors.newSingleThreadExecutor();
        accountLockService = new AccountLockService(1);
    }

    @Test
    void acquire_read_lock() throws ExecutionException, InterruptedException {
        var lock = firstThread(this::lockSampleTransferRequest);
        assertThat(lock).isNotNull();
        firstThreadUnlocks(lock);
    }

    @Test
    void acquire_read_lock_from_multiple_threads_for_read_and_fail_when_try_to_acquire_write_lock()
            throws ExecutionException, InterruptedException {
        var lock = firstThread(this::lockAnAccountForRead);
        var secondLock = secondThread(this::lockAnAccountForRead);

        assertThatThrownAccountLockException(firstThreadExecutor, this::lockSampleTransferRequest);
        firstThreadUnlocks(lock);
        assertThatThrownAccountLockException(firstThreadExecutor, this::lockSampleTransferRequest);
        secondThreadUnlocks(secondLock);
        assertThat(firstThread(this::lockSampleTransferRequest));
    }

    @Test
    void fail_to_acquire_lock_if_from_account_is_locked() throws ExecutionException, InterruptedException {
        var lock = firstThread(this::lockAnAccountForRead);
        assertThatThrownAccountLockException(firstThreadExecutor, this::lockSampleTransferRequest);
        firstThreadUnlocks(lock);
        assertThat(firstThread(this::lockSampleTransferRequest)).isNotNull();
    }

    @Test
    void fail_to_acquire_lock_if_to_account_is_locked() throws ExecutionException, InterruptedException {
        var lock = firstThread(this::lockAnotherAccountForRead);
        assertThatThrownAccountLockException(firstThreadExecutor, this::lockSampleTransferRequest);
        firstThreadUnlocks(lock);
        firstThread(this::lockSampleTransferRequest);
    }

    private AccountLock lockSampleTransferRequest() {
        return accountLockService.lockForWrite(SAMPLE_TRANSFER_REQUEST);
    }

    private AccountLock lockAnAccountForRead() {
        return accountLockService.lockForRead(AN_ACCOUNT_ID);
    }

    private AccountLock lockAnotherAccountForRead() {
        return accountLockService.lockForRead(ANOTHER_ACCOUNT_ID);
    }

    private <X> X firstThread(Callable<X> producer) throws ExecutionException, InterruptedException {
        return firstThreadExecutor.submit(producer).get();
    }

    private <X> X secondThread(Callable<X> producer) throws ExecutionException, InterruptedException {
        return secondThreadExecutor.submit(producer).get();
    }

    private void firstThreadUnlocks(AccountLock lock) throws ExecutionException, InterruptedException {
        firstThreadExecutor.submit(lock::unlock).get();
    }

    private void secondThreadUnlocks(AccountLock lock) throws ExecutionException, InterruptedException {
        secondThreadExecutor.submit(lock::unlock).get();
    }

    private static void assertThatThrownAccountLockException(ExecutorService executorService, Runnable lockExecution) {
        assertThatThrownBy(() -> executorService.submit(lockExecution).get())
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(AccountLockException.class);
    }
}
