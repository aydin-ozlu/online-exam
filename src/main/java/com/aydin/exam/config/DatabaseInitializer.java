package com.aydin.exam.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DatabaseInitializer {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    public static void initialize() {
        try (Connection conn = Database.getDataSource().getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS exams (
                  id INT PRIMARY KEY AUTO_INCREMENT,
                  title VARCHAR(255) NOT NULL
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS questions (
                  id INT PRIMARY KEY AUTO_INCREMENT,
                  exam_id INT NOT NULL,
                  question_text TEXT NOT NULL,
                  question_type VARCHAR(50) NOT NULL,
                  text_answer TEXT DEFAULT NULL,
                  FOREIGN KEY (exam_id) REFERENCES exams(id)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS choices (
                  id INT PRIMARY KEY AUTO_INCREMENT,
                  question_id INT NOT NULL,
                  choice_text VARCHAR(255) NOT NULL,
                  is_correct BOOLEAN NOT NULL DEFAULT FALSE,
                  FOREIGN KEY (question_id) REFERENCES questions(id)
                )
            """);

            logger.info("Database tables created or already exist.");
            
            if (!hasExams(conn)) {
                insertSampleData(conn);
                logger.info("Sample data inserted.");
            } else {
            	logger.info("Sample data already exists, skipping insertion.");
            }

        } catch (SQLException e) {
        	logger.error("Database initialization failed: " + e.getMessage());
        }
    }

    private static boolean hasExams(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM exams");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    private static void insertSampleData(Connection conn) throws SQLException { 
        String insertExamSQL = "INSERT INTO exams (title) VALUES (?)";
        int examId;
        try (PreparedStatement ps = conn.prepareStatement(insertExamSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Simple Math Exam");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                examId = rs.getInt(1);
            }
        }
        
        String insertQuestionSQL = "INSERT INTO questions (exam_id, question_text, question_type, text_answer) VALUES (?, ?, ?, ?)";
        
        int[] questionIds = new int[5];

        try (PreparedStatement ps = conn.prepareStatement(insertQuestionSQL, Statement.RETURN_GENERATED_KEYS)) {
        	ps.setInt(1, examId);
        	ps.setString(2, "What is 2 + 2?");
        	ps.setString(3, "multiple_choice");
        	 ps.setString(4, null);
        	ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                questionIds[0] = rs.getInt(1);
            }
            
            ps.setInt(1, examId);
            ps.setString(2, "Write answer of 9 * 7 = ?");
            ps.setString(3, "essay");
            ps.setString(4, "63");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                questionIds[4] = rs.getInt(1);
            }
            
            ps.setInt(1, examId);
            ps.setString(2, "Which number is prime?");
            ps.setString(3, "multiple_choice");
            ps.setString(4, null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                questionIds[1] = rs.getInt(1);
            }

            ps.setInt(1, examId);
            ps.setString(2, "Solve: 5 * 6 = ?");
            ps.setString(3, "multiple_choice");
            ps.setString(4, null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                questionIds[2] = rs.getInt(1);
            }

            ps.setInt(1, examId);
            ps.setString(2, "What is the derivative of x^2?");
            ps.setString(3, "multiple_choice");
            ps.setString(4, null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                questionIds[3] = rs.getInt(1);
            }            
        }

        String insertChoiceSQL = "INSERT INTO choices (question_id, choice_text, is_correct) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertChoiceSQL)) {
            ps.setInt(1, questionIds[0]);
            ps.setString(2, "3");
            ps.setBoolean(3, false);
            ps.executeUpdate();

            ps.setInt(1, questionIds[0]);
            ps.setString(2, "4");
            ps.setBoolean(3, true);
            ps.executeUpdate();

            ps.setInt(1, questionIds[0]);
            ps.setString(2, "5");
            ps.setBoolean(3, false);
            ps.executeUpdate();

            ps.setInt(1, questionIds[1]);
            ps.setString(2, "4");
            ps.setBoolean(3, false);
            ps.executeUpdate();

            ps.setInt(1, questionIds[1]);
            ps.setString(2, "7");
            ps.setBoolean(3, true);
            ps.executeUpdate();

            ps.setInt(1, questionIds[1]);
            ps.setString(2, "9");
            ps.setBoolean(3, false);
            ps.executeUpdate();

            ps.setInt(1, questionIds[2]);
            ps.setString(2, "11");
            ps.setBoolean(3, false);
            ps.executeUpdate();

            ps.setInt(1, questionIds[2]);
            ps.setString(2, "30");
            ps.setBoolean(3, true);
            ps.executeUpdate();

            ps.setInt(1, questionIds[2]);
            ps.setString(2, "56");
            ps.setBoolean(3, false);
            ps.executeUpdate();

            ps.setInt(1, questionIds[3]);
            ps.setString(2, "2x");
            ps.setBoolean(3, true);
            ps.executeUpdate();

            ps.setInt(1, questionIds[3]);
            ps.setString(2, "x^2");
            ps.setBoolean(3, false);
            ps.executeUpdate();

            ps.setInt(1, questionIds[3]);
            ps.setString(2, "x");
            ps.setBoolean(3, false);
            ps.executeUpdate();
        }
    }


}
