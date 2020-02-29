package org.danielmkraus.transfer.domain;

import org.danielmkraus.transfer.exception.AccountValidationException;
import org.danielmkraus.transfer.exception.InvalidAmountException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.danielmkraus.transfer.TransferTests.*;
import static org.danielmkraus.transfer.domain.TransferRequest.aTransfer;

@Tag(UNIT_TEST)
class TransferRequestTest {

    @Test
    void creates_transfer_request(){
        var transfer = aTransfer()
                .from(AN_ACCOUNT_ID)
                .to(ANOTHER_ACCOUNT_ID)
                .ofAmount(TEN);

        assertThat(transfer.getAmount()).isEqualTo(TEN);
        assertThat(transfer.getFromAccountId()).isEqualTo(AN_ACCOUNT_ID);
        assertThat(transfer.getToAccountId()).isEqualTo(ANOTHER_ACCOUNT_ID);
    }

    @Test
    void fail_to_transfer_to_same_account(){
        assertThatThrownBy(()->
                aTransfer()
                        .from(AN_ACCOUNT_ID)
                        .to(AN_ACCOUNT_ID)
                        .ofAmount(TEN))
                .isInstanceOf(AccountValidationException.class);

    }

    @Test
    void fail_to_transfer_negative_amount(){
        BigDecimal negativeAmount = new BigDecimal("-0.01");

        assertThatThrownBy(()->
                aTransfer()
                        .from(AN_ACCOUNT_ID)
                        .to(ANOTHER_ACCOUNT_ID)
                        .ofAmount(negativeAmount))
                .isInstanceOf(InvalidAmountException.class);

    }

    @Test
    void fail_to_transfer_zero(){
        assertThatThrownBy(()->
                aTransfer()
                        .from(AN_ACCOUNT_ID)
                        .to(ANOTHER_ACCOUNT_ID)
                        .ofAmount(ZERO))
                .isInstanceOf(InvalidAmountException.class);
    }
}