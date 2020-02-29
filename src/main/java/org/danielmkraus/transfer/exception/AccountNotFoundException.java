package org.danielmkraus.transfer.exception;

import javax.ws.rs.WebApplicationException;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class AccountNotFoundException extends WebApplicationException {
    public AccountNotFoundException() {
        super(NOT_FOUND);
    }
}
