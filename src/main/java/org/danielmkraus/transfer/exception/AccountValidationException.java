package org.danielmkraus.transfer.exception;

import javax.ws.rs.WebApplicationException;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class AccountValidationException extends WebApplicationException {
    public AccountValidationException(String message) {
        super(message, BAD_REQUEST);
    }
}
