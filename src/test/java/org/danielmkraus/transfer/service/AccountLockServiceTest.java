package org.danielmkraus.transfer.service;

import org.danielmkraus.transfer.domain.TransferRequest;
import org.danielmkraus.transfer.exception.AccountLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.State.TIMED_WAITING;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
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
        accountLockService = new AccountLockService(5);
    }

    @Test
    void acquire_read_lock() throws ExecutionException, InterruptedException {
        assertThat(
                firstThread(() -> accountLockService.lockForRead(
                        AN_ACCOUNT_ID,
                        this::dummySupplier)).get())
                .isNull();
    }

    @Test
    void acquire_read_lock_from_multiple_threads_for_read_and_fail_when_try_to_acquire_write_lock()
            throws InterruptedException {
        var semaphore = createSemaphore();
        firstThread(() -> accountLockService.lockForRead(AN_ACCOUNT_ID, () -> acquireLockAndReturn(semaphore)));
        firstThread(() -> accountLockService.lockForRead(AN_ACCOUNT_ID, this::dummySupplier));
        accountLockService.lockForRead(AN_ACCOUNT_ID, this::dummySupplier);
        waitForSemaphoreBeReached(semaphore);
        assertThatThrownAccountLockException(secondThreadExecutor, System.out::println);
    }

    @Test
    void fail_to_acquire_lock_if_from_account_is_locked() throws InterruptedException {
        var semaphore = createSemaphore();
        firstThread(() -> accountLockService.lockForWrite(AN_ACCOUNT_ID, () -> acquireLock(semaphore)));
        waitForSemaphoreBeReached(semaphore);
        assertThatThrownAccountLockException(secondThreadExecutor, System.out::println);
    }

    @Test
    void fail_to_acquire_lock_if_to_account_is_locked() throws InterruptedException {
        var semaphore = createSemaphore();
        firstThread(() -> accountLockService.lockForWrite(ANOTHER_ACCOUNT_ID, () -> acquireLock(semaphore)));
        waitForSemaphoreBeReached(semaphore);
        assertThatThrownAccountLockException(secondThreadExecutor, System.out::println);
    }

    @Test
    void fail_to_execute_when_thread_is_interrupted() throws InterruptedException {
        accountLockService = new AccountLockService(Long.MAX_VALUE);
        var semaphore = createSemaphore();
        firstThread(() -> accountLockService.lockForWrite(AN_ACCOUNT_ID, () -> acquireLock(semaphore)));
        waitForSemaphoreBeReached(semaphore);

        Thread secondThread = new Thread(() -> accountLockService.lockForWrite(AN_ACCOUNT_ID, () -> {}));

        AtomicInteger fails = new AtomicInteger();
        secondThread.setUncaughtExceptionHandler((thread, throwable) -> {
            fails.incrementAndGet();
            assertThat(throwable).isInstanceOf(AccountLockException.class);
        });

        secondThread.start();
        await().atMost(2, TimeUnit.SECONDS).until(()-> secondThread.getState() == TIMED_WAITING);

        secondThread.interrupt();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted( ()->assertThat(fails).hasValue(1));
    }

    private void waitForSemaphoreBeReached(Semaphore semaphore) {
        await()
                .atMost(2, TimeUnit.SECONDS)
                .until(() -> semaphore.getQueueLength() > 0);
    }

    private Semaphore createSemaphore() throws InterruptedException {
        var semaphore = new Semaphore(1);
        semaphore.acquire();
        return semaphore;
    }

    private <X> Future<X> firstThread(Callable<X> callable) {
        return firstThreadExecutor.submit(callable);
    }

    private Future<?> firstThread(Runnable runnable) {
        return firstThreadExecutor.submit(runnable);
    }

    private void assertThatThrownAccountLockException(ExecutorService executorService, Runnable lockExecution) {
        assertThatThrownBy(() -> executorService.submit(() -> accountLockService.lockForWrite(SAMPLE_TRANSFER_REQUEST, lockExecution)).get())
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(AccountLockException.class);
    }

    private Void dummySupplier() {
        return null;
    }

    private Integer acquireLockAndReturn(Semaphore semaphore) {
        acquireLock(semaphore);
        return 1;
    }

    private void acquireLock(Semaphore semaphore) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
