package org.danielmkraus.transfer.domain;

import org.danielmkraus.transfer.exception.AccountValidationException;
import org.danielmkraus.transfer.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class TransferRequest {
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;

    TransferRequest() {
    }

    private TransferRequest(String fromAccountId, String toAccountId, BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public static TransferRequestBuilder aTransfer() {
        return new TransferRequestBuilder();
    }


    public void validate() {
        validateAccountIds();
        validateAmount();
    }

    private void validateAccountIds() {
        requireNonNull(fromAccountId);
        requireNonNull(toAccountId);
        if (fromAccountId.equals(toAccountId)) {
            throw new AccountValidationException("Source and destination transfer should be different");
        }
    }

    private void validateAmount() {
        requireNonNull(amount);
        boolean amountLessThanOrEqualZero = amount.compareTo(BigDecimal.ZERO) <= 0;
        if (amountLessThanOrEqualZero) {
            throw new InvalidAmountException();
        }
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferRequest that = (TransferRequest) o;
        return Objects.equals(fromAccountId, that.fromAccountId) &&
                Objects.equals(toAccountId, that.toAccountId) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromAccountId, toAccountId, amount);
    }

    @Override
    public String toString() {
        return "TransferRequest{" +
                "fromAccountId='" + fromAccountId + '\'' +
                ", toAccountId='" + toAccountId + '\'' +
                ", amount=" + amount +
                '}';
    }

    public static final class TransferRequestBuilder {
        private String fromAccountId;
        private String toAccountId;

        public TransferRequestBuilder from(String fromAccountId) {
            this.fromAccountId = fromAccountId;
            return this;
        }

        public TransferRequestBuilder to(String toAccountId) {
            this.toAccountId = toAccountId;
            return this;
        }

        public TransferRequest ofAmount(BigDecimal amount) {
            TransferRequest transferRequest = new TransferRequest(fromAccountId, toAccountId, amount);
            transferRequest.validate();
            return transferRequest;

        }
    }
}
