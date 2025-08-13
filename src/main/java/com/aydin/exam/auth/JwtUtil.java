package com.aydin.exam.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET = "superSecretKey123!"; // gizli anahtar, prod’da güvenle saklanmalı
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String generateToken(String username, long expirationMillis) throws Exception {
        long now = System.currentTimeMillis();
        long exp = now + expirationMillis;

        Map<String, Object> header = Map.of(
            "alg", "HS256",
            "typ", "JWT"
        );

        Map<String, Object> payload = Map.of(
            "sub", username,
            "iat", now,
            "exp", exp
        );

        String headerJson = mapper.writeValueAsString(header);
        String payloadJson = mapper.writeValueAsString(payload);

        String headerBase64 = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payloadBase64 = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

        String unsignedToken = headerBase64 + "." + payloadBase64;
        String signature = hmacSha256(unsignedToken, SECRET);

        return unsignedToken + "." + signature;
    }

    public static boolean validateToken(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }

        String unsignedToken = parts[0] + "." + parts[1];
        String signature = parts[2];

        String expectedSignature = hmacSha256(unsignedToken, SECRET);
        if (!expectedSignature.equals(signature)) {
            return false;
        }

        String payloadJson = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
        Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);

        long exp = ((Number) payload.get("exp")).longValue();
        long now = System.currentTimeMillis();

        return now < exp;
    }

    public static String getUsername(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }
        String payloadJson = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
        Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);
        return (String) payload.get("sub");
    }

    private static String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(hash);
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String str) {
        return Base64.getUrlDecoder().decode(str);
    }
}
