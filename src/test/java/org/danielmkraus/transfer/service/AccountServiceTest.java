package org.danielmkraus.transfer.service;

import org.danielmkraus.transfer.exception.AccountAlreadyRegisteredException;
import org.danielmkraus.transfer.exception.AccountNotFoundException;
import org.danielmkraus.transfer.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.danielmkraus.transfer.TransferTests.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag(UNIT_TEST)
class AccountServiceTest {

    @Mock
    private AccountRepository repository;
    private AccountService accountService;

    @BeforeEach
    void setup() {
        accountService = new AccountService(repository, new AccountLockService(500));
    }

    @Test
    void fail_when_get_unregistered() {
        when(repository.getById(UNREGISTERED_ID)).thenThrow(AccountNotFoundException.class);
        assertThatThrownBy(() -> accountService.get(UNREGISTERED_ID))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void get() {
        when(repository.getById(SAMPLE_ACCOUNT.getId())).thenReturn(SAMPLE_ACCOUNT);
        assertThat(accountService.get(SAMPLE_ACCOUNT.getId())).isEqualTo(SAMPLE_ACCOUNT);
    }

    @Test
    void fail_when_register_existing_account() {
        when(repository.findById(SAMPLE_ACCOUNT.getId())).thenReturn(Optional.of(SAMPLE_ACCOUNT));
        assertThatThrownBy(() -> accountService.register(SAMPLE_ACCOUNT.getId()))
                .isInstanceOf(AccountAlreadyRegisteredException.class);
    }

    @Test
    void register_new_account() {
        when(repository.findById(SAMPLE_ACCOUNT.getId())).thenReturn(Optional.empty());

        accountService.register(SAMPLE_ACCOUNT.getId());

        verify(repository).save(
                argThat(arg -> Objects.equals(arg.getId(), SAMPLE_ACCOUNT.getId()) &&
                        Objects.equals(arg.getBalance(), ZERO)));
    }

    @Test
    void set_new_account() {
        accountService.set(SAMPLE_ACCOUNT.getId(), TEN);

        verify(repository).save(
                argThat(arg -> Objects.equals(arg.getId(), SAMPLE_ACCOUNT.getId()) &&
                        Objects.equals(arg.getBalance(), TEN)));

    }

    @Test
    void set_balance_on_existing_account() {
        when(repository.findById(SAMPLE_ACCOUNT.getId())).thenReturn(Optional.empty());

        accountService.register(SAMPLE_ACCOUNT.getId());

        verify(repository).save(
                argThat(arg -> Objects.equals(arg.getId(), SAMPLE_ACCOUNT.getId()) &&
                        Objects.equals(arg.getBalance(), ZERO)));

        accountService.set(SAMPLE_ACCOUNT.getId(), TEN);

        verify(repository).save(
                argThat(arg -> Objects.equals(arg.getId(), SAMPLE_ACCOUNT.getId()) &&
                        Objects.equals(arg.getBalance(), TEN)));
    }
}