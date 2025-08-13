package com.aydin.exam.model;

public class Choice {
    private int id;
    private String choiceText;
    private boolean isCorrect;  // yeni alan

    public int getId() { 
        return id;
    }

    public void setId(int id) { 
        this.id = id;
    }

    public String getChoiceText() { 
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
