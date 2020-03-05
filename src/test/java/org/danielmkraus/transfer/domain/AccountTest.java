package org.danielmkraus.transfer.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.math.BigDecimal.ZERO;
import static nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.danielmkraus.transfer.TransferTests.UNIT_TEST;
import static org.danielmkraus.transfer.domain.Account.newAccount;

@Tag(UNIT_TEST)
class AccountTest {
    @Test
    void fail_when_create_an_account_with_null_id() {
        assertThatThrownBy(() -> newAccount(null, ZERO))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void fail_when_create_an_account_with_null_balance() {
        assertThatThrownBy(() -> newAccount(null, ZERO))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void equalsContract() {
        EqualsVerifier.forClass(Account.class)
                .withOnlyTheseFields("id")
                .suppress(NONFINAL_FIELDS)
                .verify();
    }
}
