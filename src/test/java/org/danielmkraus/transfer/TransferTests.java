package org.danielmkraus.transfer;

import org.danielmkraus.transfer.domain.Account;

import static java.math.BigDecimal.ZERO;

public interface TransferTests {
    String UNIT_TEST = "unit-test";
    String INTEGRATION_TEST = "integration-test";

    String AN_ACCOUNT_ID = "1";
    String ANOTHER_ACCOUNT_ID = "2";
    String UNREGISTERED_ID = "UNREGISTERED";
    Account SAMPLE_ACCOUNT = new Account(AN_ACCOUNT_ID, ZERO);
}
