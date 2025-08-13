package com.aydin.exam.model;

import java.util.List;

public class MultipleChoiceQuestion extends Question {
    private List<Choice> choices;

    public List<Choice> getChoices() { 
    	return choices; 
    }
    
    public void setChoices(List<Choice> choices) { 
    	this.choices = choices; 
    }
}