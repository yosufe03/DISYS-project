package org.example.gui;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;

public class ApiClient {
    private static final String API_URL = "http://localhost:8080/energy";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public CompletableFuture<String> getAsync(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        JSONObject errorResponse = new JSONObject(response.body());
                        String errorMessage = errorResponse.optString("message", "Unknown error");
                        throw new RuntimeException(response.statusCode() + ": " + errorMessage);
                    }
                    return response.body();
                });
    }
}
