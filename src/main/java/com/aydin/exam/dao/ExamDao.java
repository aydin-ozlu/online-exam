package com.aydin.exam.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.aydin.exam.config.Database;
import com.aydin.exam.model.Choice;
import com.aydin.exam.model.EssayQuestion;
import com.aydin.exam.model.Exam;
import com.aydin.exam.model.MultipleChoiceQuestion;
import com.aydin.exam.model.Question;

public class ExamDao {
    public Exam findById(int examId) throws SQLException {
        Exam exam = null;
        String examSql = "SELECT id, title FROM exams WHERE id = ?";
        String questionSql = "SELECT id, question_text, question_type, text_answer FROM questions WHERE exam_id = ?";
        String choiceSql = "SELECT id, choice_text, is_correct FROM choices WHERE question_id = ?";

        try (Connection conn = Database.getDataSource().getConnection();
             PreparedStatement examStmt = conn.prepareStatement(examSql);
             PreparedStatement questionStmt = conn.prepareStatement(questionSql);
             PreparedStatement choiceStmt = conn.prepareStatement(choiceSql)) {

            examStmt.setInt(1, examId);
            try (ResultSet examRs = examStmt.executeQuery()) {
                if (!examRs.next()) {
                    return null; // Sınav bulunamadı
                }
                exam = new Exam();
                exam.setId(examRs.getInt("id"));
                exam.setTitle(examRs.getString("title"));
            }

            questionStmt.setInt(1, examId);
            try (ResultSet questionRs = questionStmt.executeQuery()) {
                List<Question> questions = new ArrayList<>();

                while (questionRs.next()) {
                    int qId = questionRs.getInt("id");
                    String qText = questionRs.getString("question_text");
                    String qType = questionRs.getString("question_type");
                    String qAnswer = questionRs.getString("text_answer");

                    Question question;
                    if ("multiple_choice".equalsIgnoreCase(qType)) {
                        MultipleChoiceQuestion mcq = new MultipleChoiceQuestion();
                        mcq.setId(qId);
                        mcq.setQuestionText(qText);
                        mcq.setQuestionType(qType);

                        choiceStmt.setInt(1, qId);
                        try (ResultSet choiceRs = choiceStmt.executeQuery()) {
                            List<Choice> choices = new ArrayList<>();
                            while (choiceRs.next()) {
                                Choice choice = new Choice();
                                choice.setId(choiceRs.getInt("id"));
                                choice.setChoiceText(choiceRs.getString("choice_text"));
                                choice.setCorrect(choiceRs.getBoolean("is_correct"));
                                choices.add(choice);
                            }
                            mcq.setChoices(choices);
                        }

                        question = mcq;
                    } else {
                        EssayQuestion essay = new EssayQuestion();
                        essay.setId(qId);
                        essay.setQuestionText(qText);
                        essay.setQuestionType(qType);
                        essay.setTextAnswer(qAnswer);
                        question = essay;
                    }
                    questions.add(question);
                }
                exam.setQuestions(questions);
            }
        }
        return exam;
    }
}
