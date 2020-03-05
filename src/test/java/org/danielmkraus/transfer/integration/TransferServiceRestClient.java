package org.danielmkraus.transfer.integration;

import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Callable;

import static java.util.Optional.ofNullable;

public class TransferServiceRestClient {
    private static final String ACCOUNTS_ENDPOINT = "/rest/accounts/";
    private static final String TRANSFERS_ENDPOINT = "/rest/transfers/";
    private final String serverUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    public TransferServiceRestClient(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public HttpResponse<String> setBalance(String accountId, BigDecimal balance) {
        var request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(serverUrl + ACCOUNTS_ENDPOINT +
                        accountId +
                        ofNullable(balance)
                                .map(v->"?balance=" + v)
                                .orElse("")))
                .build();
        return execute(() -> httpClient.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    public HttpResponse<String> getAccount(String accountId) {
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(serverUrl + ACCOUNTS_ENDPOINT + accountId))
                .build();
        return execute(() -> httpClient.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    public HttpResponse<String> createAccount(String accountId) {
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(serverUrl + ACCOUNTS_ENDPOINT + accountId))
                .build();
        return execute(() -> httpClient.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    public HttpResponse<String> transfer(String from, String to, BigDecimal amount) {
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{ \"fromAccountId\": \"" + from +
                        "\", \"toAccountId\": \"" + to +
                        "\",\"amount\":\"" + amount + "\"}"))
                .uri(URI.create(serverUrl + TRANSFERS_ENDPOINT))
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .build();
        return execute(() -> httpClient.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    <X> X execute(Callable<X> call) {
        try {
            return call.call();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
