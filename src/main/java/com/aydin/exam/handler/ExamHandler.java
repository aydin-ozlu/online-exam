package com.aydin.exam.handler;

import com.aydin.exam.auth.AuthUtil;
import com.aydin.exam.dao.ExamDao;
import com.aydin.exam.model.Exam;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExamHandler implements HttpHandler {
	private static final Logger logger = LoggerFactory.getLogger(ExamHandler.class);
	private final ExamDao examDao = new ExamDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    	if (!AuthUtil.isAuthorized(exchange)) {
            sendResponse(exchange, 401, "Unauthorized: Missing or invalid token");
            return;
        }
    	
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        URI requestUri = exchange.getRequestURI();
        String path = requestUri.getPath(); // /exams/{id}

        String[] parts = path.split("/");
        if (parts.length != 3) {
            sendResponse(exchange, 400, "Bad Request");
            return;
        }

        String idStr = parts[2];
        int examId;
        try {
            examId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid exam ID");
            return;
        }

        try {
            Exam exam = examDao.findById(examId);
            if (exam == null) {
                sendResponse(exchange, 404, "Exam not found");
                return;
            }

            String jsonResponse = objectMapper.writeValueAsString(exam);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            sendResponse(exchange, 200, jsonResponse);

        } catch (SQLException e) {
        	logger.error("Exception caught in ExamHandler", e);
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
