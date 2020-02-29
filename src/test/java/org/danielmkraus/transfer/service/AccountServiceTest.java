package org.danielmkraus.transfer.service;

import org.danielmkraus.transfer.exception.AccountAlreadyRegisteredException;
import org.danielmkraus.transfer.exception.AccountNotFoundException;
import org.danielmkraus.transfer.repository.AccountRepository;
import org.danielmkraus.transfer.service.AccountLockService.AccountLock;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.danielmkraus.transfer.TransferTests.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag(UNIT_TEST)
class AccountServiceTest {

    @Mock
    private AccountLockService lockService;

    @Mock
    private AccountLock lock;

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountService accountService;


    @Test
    void fail_when_get_unregistered() {
        when(lockService.lockForRead(anyString())).thenReturn(lock);

        when(repository.getById(UNREGISTERED_ID)).thenThrow(AccountNotFoundException.class);
        assertThatThrownBy(() -> accountService.get(UNREGISTERED_ID))
                .isInstanceOf(AccountNotFoundException.class);
        assertThatWasLockedForReadAndUnlocked(UNREGISTERED_ID);
    }

    @Test
    void get() {
        when(lockService.lockForRead(anyString())).thenReturn(lock);

        when(repository.getById(SAMPLE_ACCOUNT.getId())).thenReturn(SAMPLE_ACCOUNT);
        assertThat(accountService.get(SAMPLE_ACCOUNT.getId())).isEqualTo(SAMPLE_ACCOUNT);
        assertThatWasLockedForReadAndUnlocked(SAMPLE_ACCOUNT.getId());
    }

    @Test
    void fail_when_register_existing_account() {
        when(repository.findById(SAMPLE_ACCOUNT.getId())).thenReturn(Optional.of(SAMPLE_ACCOUNT));
        assertThatThrownBy(() -> accountService.register(SAMPLE_ACCOUNT.getId()))
                .isInstanceOf(AccountAlreadyRegisteredException.class);
    }

    @Test
    void register_new_account() {
        when(lockService.lockForWrite(anyString())).thenReturn(lock);
        when(repository.findById(SAMPLE_ACCOUNT.getId())).thenReturn(Optional.empty());

        accountService.register(SAMPLE_ACCOUNT.getId());

        verify(repository).save(
                argThat(arg -> Objects.equals(arg.getId(), SAMPLE_ACCOUNT.getId()) &&
                        Objects.equals(arg.getBalance(), ZERO)));
        verify(lockService).lockForWrite(SAMPLE_ACCOUNT.getId());
        verify(lock).unlock();
    }

    @Test
    void set_new_account() {
        when(lockService.lockForWrite(anyString())).thenReturn(lock);
        accountService.set(SAMPLE_ACCOUNT.getId(), TEN);

        verify(repository).save(
                argThat(arg -> Objects.equals(arg.getId(), SAMPLE_ACCOUNT.getId()) &&
                        Objects.equals(arg.getBalance(), TEN)));

        verify(lockService).lockForWrite(SAMPLE_ACCOUNT.getId());
        verify(lock).unlock();
    }


    @Test
    void set_balance_on_existing_account() {
        when(lockService.lockForWrite(anyString())).thenReturn(lock);
        when(repository.findById(SAMPLE_ACCOUNT.getId())).thenReturn(Optional.empty());

        accountService.register(SAMPLE_ACCOUNT.getId());

        verify(repository).save(
                argThat(arg -> Objects.equals(arg.getId(), SAMPLE_ACCOUNT.getId()) &&
                        Objects.equals(arg.getBalance(), ZERO)));
        verify(lockService).lockForWrite(SAMPLE_ACCOUNT.getId());
        verify(lock).unlock();

        accountService.set(SAMPLE_ACCOUNT.getId(), TEN);

        verify(repository).save(
                argThat(arg -> Objects.equals(arg.getId(), SAMPLE_ACCOUNT.getId()) &&
                        Objects.equals(arg.getBalance(), TEN)));

        verify(lockService, times(2)).lockForWrite(SAMPLE_ACCOUNT.getId());
        verify(lock, times(2)).unlock();
    }

    private void assertThatWasLockedForReadAndUnlocked(String accountId) {
        verify(lockService).lockForRead(accountId);
        verify(lock).unlock();
    }
}