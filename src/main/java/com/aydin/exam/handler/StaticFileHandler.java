package com.aydin.exam.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFileHandler implements HttpHandler {
	private static final Logger logger = LoggerFactory.getLogger(StaticFileHandler.class);
    private final String basePath;

    public StaticFileHandler(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();

        if (uriPath.equals("/")) {
            uriPath = "/index.html";
        }

        Path filePath = null;
        
        try {
        	filePath = Paths.get(basePath, uriPath);
        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }
        } catch(Exception ex) {
        	logger.error("Exception caught in StaticFileHandler", ex);
        }

        String contentType = URLConnection.guessContentTypeFromName(filePath.toString());
        if (contentType == null) contentType = "application/octet-stream";

        exchange.getResponseHeaders().add("Content-Type", contentType);
        byte[] bytes = Files.readAllBytes(filePath);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
