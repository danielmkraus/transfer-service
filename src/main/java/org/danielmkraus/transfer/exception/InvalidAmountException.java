package org.danielmkraus.transfer.exception;

import javax.ws.rs.WebApplicationException;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidAmountException extends WebApplicationException {
    public InvalidAmountException() {
        super("Transfer amount should be greater than zero", BAD_REQUEST);
    }
}
