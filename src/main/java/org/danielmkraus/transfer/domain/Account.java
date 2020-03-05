package org.danielmkraus.transfer.domain;

import java.math.BigDecimal;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Account {
    private String id;
    private BigDecimal balance;

    public static Account newAccount(String id, BigDecimal balance) {
        Account account = new Account(id, balance);
        account.validate();
        return account;
    }

    public Account(String id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    private void validate() {
        requireNonNull(id);
        requireNonNull(balance);
    }

    public boolean isBalanceLessThan(BigDecimal amount) {
        return amount.compareTo(balance) > 0;
    }

    public void subtractBalance(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public void addBalance(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getId() {
        return id;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", balance=" + balance +
                '}';
    }
}
