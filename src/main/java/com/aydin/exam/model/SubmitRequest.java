package com.aydin.exam.model;

import java.util.Map;

public class SubmitRequest {
    private int examId;
    private Map<Integer, String> answers; // key=questionId, value=cevap (choiceId veya metin)

    public int getExamId() { 
    	return examId; 
    }
    
    public void setExamId(int examId) { 
    	this.examId = examId; 
    }

    public Map<Integer, String> getAnswers() { 
    	return answers; 
    }
    
    public void setAnswers(Map<Integer, String> answers) { 
    	this.answers = answers; 
    }
}
