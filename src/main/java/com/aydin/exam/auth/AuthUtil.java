package com.aydin.exam.auth;

import com.sun.net.httpserver.HttpExchange;

public class AuthUtil {

    public static boolean isAuthorized(HttpExchange exchange) {
        try {
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return false;
            }

            String token = authHeader.substring("Bearer ".length());
            return JwtUtil.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUsername(HttpExchange exchange) {
        try {
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null;
            }

            String token = authHeader.substring("Bearer ".length());
            return JwtUtil.getUsername(token);
        } catch (Exception e) {
            return null;
        }
    }
}
