package com.aydin.exam.handler;

import com.aydin.exam.auth.AuthUtil;
import com.aydin.exam.dao.ExamDao;
import com.aydin.exam.model.Exam;
import com.aydin.exam.model.Question;
import com.aydin.exam.model.SubmitRequest;
import com.aydin.exam.scoring.ScoringStrategy;
import com.aydin.exam.scoring.ScoringStrategyFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmitHandler implements HttpHandler {
	private static final Logger logger = LoggerFactory.getLogger(SubmitHandler.class);
	private final ExamDao examDao;
	private final ObjectMapper objectMapper;
    
	public SubmitHandler() {
        this.examDao = new ExamDao();
        this.objectMapper = new ObjectMapper();
    }
	
    public SubmitHandler(ExamDao examDao, ObjectMapper objectMapper) {
        this.examDao = examDao;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    	if (!AuthUtil.isAuthorized(exchange)) {
            sendResponse(exchange, 401, "Unauthorized: Missing or invalid token");
            return;
        }
    	
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try (InputStream is = exchange.getRequestBody()) {
            SubmitRequest submitRequest = objectMapper.readValue(is, SubmitRequest.class);

            Exam exam = examDao.findById(submitRequest.getExamId());
            if (exam == null) {
                sendResponse(exchange, 404, "Exam not found");
                return;
            }

            Map<Integer, String> answers = submitRequest.getAnswers();
            if (answers == null) {
                sendResponse(exchange, 400, "Answers required");
                return;
            }

            double totalScore = 0;
            for (Question question : exam.getQuestions()) {
                String userAnswer = answers.get(question.getId());
                if (userAnswer == null) continue;

                ScoringStrategy strategy = ScoringStrategyFactory.getStrategy(question.getQuestionType());
                totalScore += strategy.score(question, userAnswer);
            }
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("score", totalScore);
            String jsonResponse = objectMapper.writeValueAsString(responseMap);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            sendResponse(exchange, 200, jsonResponse);


        } catch (IOException | SQLException e) {
        	logger.error("Exception caught in SubmitHandler", e);
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
