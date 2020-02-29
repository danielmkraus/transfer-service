package org.danielmkraus.transfer.exception;

import javax.ws.rs.WebApplicationException;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class AccountAlreadyRegisteredException extends WebApplicationException {
    public AccountAlreadyRegisteredException() {
        super("Account already registered", BAD_REQUEST);
    }
}
