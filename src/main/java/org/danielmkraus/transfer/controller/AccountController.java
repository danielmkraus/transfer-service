package org.danielmkraus.transfer.controller;

import org.danielmkraus.transfer.domain.Account;
import org.danielmkraus.transfer.service.AccountService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

@Path("accounts/{id}")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Account get(@PathParam("id") String accountId) {
        return accountService.get(accountId);
    }

    @POST
    public void register(@PathParam("id") String accountId) {
        accountService.register(accountId);
    }

    @PUT
    public void set(@PathParam("id") String accountId, @QueryParam("balance") BigDecimal balance) {
        accountService.set(accountId, balance);
    }
}
