package org.danielmkraus.transfer.service;

import org.danielmkraus.transfer.domain.Account;
import org.danielmkraus.transfer.exception.InsufficientFundsException;
import org.danielmkraus.transfer.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.danielmkraus.transfer.TransferTests.UNIT_TEST;
import static org.danielmkraus.transfer.domain.TransferRequest.aTransfer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag(UNIT_TEST)
class TransferServiceTest {
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final BigDecimal TWENTY = new BigDecimal("20");

    @Mock
    private AccountRepository repository;

    private TransferService service;

    @BeforeEach
    void setup() {
        when(repository.getById(FROM))
                .thenReturn(new Account(FROM, TEN));
        when(repository.getById(TO))
                .thenReturn(new Account(TO, TEN));

        service = new TransferService(repository, new AccountLockService(500));
    }

    @Test
    void transfer() {
        service.transfer(
                aTransfer()
                        .from(FROM)
                        .to(TO)
                        .ofAmount(TEN));

        assertThat(balanceOf(FROM)).isEqualTo(ZERO);
        assertThat(balanceOf(TO)).isEqualTo(TWENTY);
    }

    @Test
    void fail_to_transfer_more_than_account_balance() {
        BigDecimal amountGreaterThanSourceAccount = new BigDecimal("10.01");
        assertThat(amountGreaterThanSourceAccount).isGreaterThan(balanceOf(FROM));

        assertThatThrownBy(() -> service.transfer(
                aTransfer()
                        .from(FROM)
                        .to(TO)
                        .ofAmount(amountGreaterThanSourceAccount)))
                .isInstanceOf(InsufficientFundsException.class);

        assertThatAccountBalancesWasNotChanged();
    }

    private void assertThatAccountBalancesWasNotChanged() {
        assertThat(balanceOf(FROM)).isEqualTo(TEN);
        assertThat(balanceOf(TO)).isEqualTo(TEN);
    }

    private BigDecimal balanceOf(String accountId) {
        return repository.getById(accountId).getBalance();
    }
}