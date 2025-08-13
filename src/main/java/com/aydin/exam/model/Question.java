package com.aydin.exam.model;

public abstract class Question {
    protected int id;
    protected String questionText;
    protected String questionType;
    
    public int getId() { 
    	return id; 
    }
    
    public void setId(int id) { 
    	this.id = id; 
    }

    public String getQuestionText() { 
    	return questionText; 
    }
    
    public void setQuestionText(String questionText) { 
    	this.questionText = questionText; 
    }

    public String getQuestionType() { 
    	return questionType; 
    }
    
    public void setQuestionType(String questionType) { 
    	this.questionType = questionType; 
    }
}