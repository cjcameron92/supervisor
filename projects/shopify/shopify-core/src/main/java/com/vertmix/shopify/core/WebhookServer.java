package com.vertmix.shopify.core;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebhookServer {

    private static final String SECRET_KEY = "your_shopify_secret_key"; // Replace with your actual secret key
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Gson gson = new Gson();

    public void startServer(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/webhook", new WebhookHandler());
            server.setExecutor(executor); // Use async executor for requests
            server.start();
            System.out.println("Webhook server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class WebhookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            executor.submit(() -> {
                try {
                    if ("POST".equals(exchange.getRequestMethod())) {
                        String hmacHeader = exchange.getRequestHeaders().getFirst("X-Shopify-Hmac-SHA256");
                        InputStream requestBody = exchange.getRequestBody();
                        byte[] bodyBytes = requestBody.readAllBytes();
                        String body = new String(bodyBytes);

                        if (validateHmac(hmacHeader, body)) {
                            System.out.println("Webhook verified. Processing data asynchronously.");
                            DataHandler.handleIncomingData(body); // Pass data to DataHandler for processing

                            String response = "Webhook received successfully";
                            exchange.sendResponseHeaders(200, response.length());
                            exchange.getResponseBody().write(response.getBytes());
                        } else {
                            System.out.println("Invalid HMAC signature, webhook rejected.");
                            exchange.sendResponseHeaders(403, -1); // Unauthorized
                        }
                    } else {
                        exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    exchange.close();
                }
            });
        }
    }

    private static boolean validateHmac(String hmacHeader, String body) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(body.getBytes());
            String calculatedHmac = Base64.getEncoder().encodeToString(hmacBytes);

            return calculatedHmac.equals(hmacHeader);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
    }
}
