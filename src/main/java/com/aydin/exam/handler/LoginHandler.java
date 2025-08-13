package com.aydin.exam.handler;

import com.aydin.exam.auth.JwtUtil;
import com.aydin.exam.auth.UserDatabase;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LoginHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            sendResponse(exchange, 401, "Unauthorized: Missing or invalid Authorization header");
            return;
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length());
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes, StandardCharsets.UTF_8);
            String[] parts = credentials.split(":", 2);

            if (parts.length != 2) {
                sendResponse(exchange, 401, "Unauthorized: Invalid credentials format");
                return;
            }

            String username = parts[0];
            String password = parts[1];

            if (!UserDatabase.isValidUser(username, password)) {
                sendResponse(exchange, 401, "Invalid username or password");
                return;
            }

            String token = JwtUtil.generateToken(username, 1000 * 60 * 120); // 120 dk geçerli token

            logger.info("User '{}' logged in, token generated", username);

            String jsonResponse = "{\"token\": \"" + token + "\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            sendResponse(exchange, 200, jsonResponse);

        } catch (Exception e) {
            logger.error("Error during login", e);
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
