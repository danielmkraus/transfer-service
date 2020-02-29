package org.danielmkraus.transfer.integration;

import io.cucumber.core.internal.gherkin.deps.com.google.gson.Gson;
import io.cucumber.core.internal.gherkin.deps.com.google.gson.GsonBuilder;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.danielmkraus.transfer.TransferServer;
import org.danielmkraus.transfer.domain.Account;

import java.math.BigDecimal;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.danielmkraus.transfer.ServerProperties.SERVER_PORT;
import static org.danielmkraus.transfer.ServerProperties.stringProperty;

public class TransferServiceSteps {
    private static Gson gson;
    private static TransferServer server;
    private static TransferServiceRestClient restClient;

    private HttpResponse<String> transferResponse;

    @Before
    public static void setup() {
        gson = new GsonBuilder().create();
        server = TransferServer.startServer();
        restClient = new TransferServiceRestClient("http://localhost:" + stringProperty(SERVER_PORT));
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Given("an account {string} with a balance of {double}")
    public void an_account_with_a_balance_of(String accountId, Double balance) {
        assertThat(
                restClient.setBalance(
                        accountId,
                        decimal(balance))
                        .statusCode())
                .isEqualTo(204);
    }

    @Given("does not exist account {string}")
    public void does_not_exist_account(String accountId) {
        assertThat(restClient.getAccount(accountId).statusCode()).isEqualTo(404);
    }

    @When("transfer {double} from {string} to {string}")
    public void transfer_from_to(Double amount, String from, String to) {
        transferResponse = restClient.transfer(
                from,
                to,
                decimal(amount));
    }

    @Then("account {string} have balance of {double}")
    public void account_have_balance_of(String accountId, Double balance) {
        HttpResponse<String> response = restClient.getAccount(accountId);

        assertThat(response.statusCode()).isEqualTo(200);
        Account account = gson.fromJson(response.body(), Account.class);
        assertThat(account.getBalance()).isEqualByComparingTo(decimal(balance));
    }

    @Then("successfully transfer")
    public void success_on_transfer() {
        assertThat(transferResponse.statusCode()).isEqualTo(204);
    }

    @Then("a validation error on transfer occur")
    public void a_validation_error_occurs() {
        assertThat(transferResponse.statusCode()).isEqualTo(400);
    }

    @Then("account is not found")
    public void account_is_not_found() {
        assertThat(transferResponse.statusCode()).isEqualTo(404);
    }

    private BigDecimal decimal(Double value) {
        return new BigDecimal(value.toString());
    }
}
