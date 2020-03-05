package org.danielmkraus.transfer.repository;

import org.danielmkraus.transfer.domain.Account;
import org.danielmkraus.transfer.exception.AccountNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.danielmkraus.transfer.TransferTests.*;

@Tag(UNIT_TEST)
class AccountRepositoryTest {
    private AccountRepository repository;

    @BeforeEach
    void setup() {
        repository = new AccountRepository();
    }


    @Test
    void get_by_id() {
        repository.save(SAMPLE_ACCOUNT);

        assertThat(repository.getById(AN_ACCOUNT_ID))
                .isNotNull()
                .extracting(Account::getId, Account::getBalance)
                .isEqualTo(asList(AN_ACCOUNT_ID, ZERO));
    }

    @Test
    void fail_to_get_by_unregistered_id() {
        assertThatThrownBy(() -> repository.getById(UNREGISTERED_ID))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void not_find_by_unregistered_id() {
        assertThat(repository.findById(UNREGISTERED_ID)).isEmpty();
    }

    @Test
    void find_by_id() {
        repository.save(SAMPLE_ACCOUNT);
        assertThat(repository.findById(AN_ACCOUNT_ID))
                .isNotEmpty().get()
                .extracting(Account::getId, Account::getBalance)
                .isEqualTo(asList(AN_ACCOUNT_ID, ZERO));
    }
}