package org.danielmkraus.transfer.exception;

import javax.ws.rs.WebApplicationException;

import static javax.ws.rs.core.Response.Status.CONFLICT;

public class AccountLockException extends WebApplicationException {
    public AccountLockException(Throwable cause) {
        super(cause, CONFLICT);
    }

    public AccountLockException() {
        super(CONFLICT);
    }
}
