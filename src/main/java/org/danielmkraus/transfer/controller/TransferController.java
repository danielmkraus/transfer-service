package org.danielmkraus.transfer.controller;

import org.danielmkraus.transfer.domain.TransferRequest;
import org.danielmkraus.transfer.service.TransferService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("transfers")
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void transfer(TransferRequest transferRequest) {
        transferService.transfer(transferRequest);
    }
}
