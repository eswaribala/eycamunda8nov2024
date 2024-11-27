package com.ey;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    private static HttpClient httpClient=HttpClient.newHttpClient();
    public static void main(String[] args) {

        HttpRequest httpRequest=HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:7074/offers/v1.0"))
                .build();

        CompletableFuture<HttpResponse<String>> completableFuture=
                httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

        completableFuture
                .thenApply(HttpResponse::body).thenAccept(data->System.out.println(data))
                .exceptionally(ex->{
                    System.out.println("Request Failed"+ex.getMessage());
                    return null;
                }).join();
    }
}