package com.aydin.exam.handler;

import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.net.httpserver.HttpExchange;

public class SafeHandler implements HttpHandler {
	private static final Logger logger = LoggerFactory.getLogger(SafeHandler.class);
    private final HttpHandler delegate;

    public SafeHandler(HttpHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            delegate.handle(exchange);
        } catch (Exception e) {
        	logger.error("Exception caught in handler", e);
            String response = "Internal Server Error";
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(500, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
