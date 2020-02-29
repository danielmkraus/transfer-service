package org.danielmkraus.transfer.exception;

import javax.ws.rs.WebApplicationException;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class InsufficientFundsException extends WebApplicationException {
    public InsufficientFundsException() {
        super("Insufficient funds", BAD_REQUEST);
    }
}
