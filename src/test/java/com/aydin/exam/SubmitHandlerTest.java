package com.aydin.exam;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.aydin.exam.dao.ExamDao;
import com.aydin.exam.handler.SubmitHandler;
import com.aydin.exam.model.Choice;
import com.aydin.exam.model.Exam;
import com.aydin.exam.model.MultipleChoiceQuestion;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class SubmitHandlerTest {

    private SubmitHandler submitHandler;
    private ExamDao mockExamDao;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockExamDao = mock(ExamDao.class);
        objectMapper = new ObjectMapper();
        submitHandler = new SubmitHandler(mockExamDao, objectMapper);
    }

    @Test
    public void testHandle_SuccessfulSubmit() throws Exception {
        // Prepare Exam with one multiple choice question and choices
        MultipleChoiceQuestion question = new MultipleChoiceQuestion();
        question.setId(1);
        question.setQuestionType("multiple_choice");
        question.setQuestionText("What is 2+2?");
        Choice choice1 = new Choice();
        choice1.setId(1);
        choice1.setChoiceText("3");
        Choice choice2 = new Choice();
        choice2.setId(2);
        choice2.setChoiceText("4");
        question.setChoices(List.of(choice1, choice2));

        Exam exam = new Exam();
        exam.setId(100);
        exam.setTitle("Math Test");
        exam.setQuestions(List.of(question));

        when(mockExamDao.findById(100)).thenReturn(exam);

        // JSON request body (answer: choiceId=2 which is correct)
        String requestBody = "{\"examId\":100,\"answers\":{\"1\":\"2\"}}";

        // Mock HttpExchange
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");

        InputStream requestStream = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(requestStream);

        Headers responseHeaders = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(responseHeaders);

        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(responseBody);

        // Capture response code argument
        doAnswer(invocation -> {
            int statusCode = invocation.getArgument(0);
            long length = invocation.getArgument(1);
            assertEquals(200, statusCode);
            return null;
        }).when(exchange).sendResponseHeaders(anyInt(), anyLong());

        // Call handle
        submitHandler.handle(exchange);

        String response = responseBody.toString(StandardCharsets.UTF_8);
        assertTrue(response.contains("\"score\""));
    }
    
    @Test
    public void testHandle_MissingAnswers() throws Exception {
        Exam exam = new Exam();
        exam.setId(100);
        exam.setTitle("Test Exam");
        exam.setQuestions(List.of());  // boş soru listesi

        when(mockExamDao.findById(100)).thenReturn(exam);

        String requestBody = "{\"examId\":100}"; // answers alanı yok

        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        InputStream requestStream = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(requestStream);

        Headers responseHeaders = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(responseHeaders);

        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(responseBody);

        doAnswer(invocation -> {
            int statusCode = invocation.getArgument(0);
            assertEquals(400, statusCode);
            return null;
        }).when(exchange).sendResponseHeaders(anyInt(), anyLong());

        submitHandler.handle(exchange);

        String response = responseBody.toString(StandardCharsets.UTF_8);
        assertTrue(response.contains("Answers required"));
    }

    @Test
    public void testHandle_InvalidMethod() throws Exception {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("GET");

        Headers responseHeaders = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(responseHeaders);

        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(responseBody);

        doAnswer(invocation -> {
            int statusCode = invocation.getArgument(0);
            assertEquals(405, statusCode);
            return null;
        }).when(exchange).sendResponseHeaders(anyInt(), anyLong());

        submitHandler.handle(exchange);

        String response = responseBody.toString(StandardCharsets.UTF_8);
        assertTrue(response.contains("Method Not Allowed"));
    }

    @Test
    public void testHandle_ExamNotFound() throws Exception {
        when(mockExamDao.findById(999)).thenReturn(null);

        String requestBody = "{\"examId\":999,\"answers\":{\"1\":\"2\"}}";

        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        InputStream requestStream = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(requestStream);

        Headers responseHeaders = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(responseHeaders);

        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(responseBody);

        doAnswer(invocation -> {
            int statusCode = invocation.getArgument(0);
            assertEquals(404, statusCode);
            return null;
        }).when(exchange).sendResponseHeaders(anyInt(), anyLong());

        submitHandler.handle(exchange);

        String response = responseBody.toString(StandardCharsets.UTF_8);
        assertTrue(response.contains("Exam not found"));
    }

}
