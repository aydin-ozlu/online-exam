package com.aydin.exam;

import com.sun.net.httpserver.HttpServer;
import com.aydin.exam.config.DatabaseInitializer;
import com.aydin.exam.handler.ExamHandler;
import com.aydin.exam.handler.LoginHandler;
import com.aydin.exam.handler.SafeHandler;
import com.aydin.exam.handler.StaticFileHandler;
import com.aydin.exam.handler.SubmitHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnlineExamServer {
	private static final Logger logger = LoggerFactory.getLogger(OnlineExamServer.class);
    private static final int PORT = 8079;
    
	public static void main(String[] args) throws IOException {
		DatabaseInitializer.initialize();
		
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/login", new SafeHandler(new LoginHandler()));
        server.createContext("/exams", new SafeHandler(new ExamHandler()));
        server.createContext("/submit", new SafeHandler(new SubmitHandler()));
        
        URI frontendUri = null;
		try {
			frontendUri = StaticFileHandler.class.getClassLoader().getResource("frontend").toURI();
		} catch (URISyntaxException e) {
			logger.error("Exception caught in main..", e);
		}
        String frontendDir = Paths.get(frontendUri).toString();

        server.createContext("/", new SafeHandler(new StaticFileHandler(frontendDir)));
        

        int corePoolSize = 10;         // minimum thread sayısı
        int maximumPoolSize = 50;      // maksimum thread sayısı
        long keepAliveTime = 60L;      // boş threadlerin bekleme süresi (saniye)
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1000); // kuyruk kapasitesi

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            workQueue,
            new ThreadPoolExecutor.CallerRunsPolicy() // kuyruk dolunca isteği submit eden thread çalıştırır
        );

        server.setExecutor(executor);
        server.start();

        logger.info("Server started on port {}", PORT);
    }
}
