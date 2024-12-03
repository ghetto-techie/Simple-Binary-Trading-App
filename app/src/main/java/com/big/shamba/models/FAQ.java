package com.big.shamba.models;

import java.io.Serializable;

public class FAQ implements Serializable {
    private String question;
    private String answer;
    private String category;

    public FAQ() {
        // Default constructor required for calls to DataSnapshot.getValue(FAQ.class)
    }

    public FAQ(String question, String answer, String category) {
        this.question = question;
        this.answer = answer;
        this.category = category;
    }

    @Override
    public String toString() {
        return "FAQ{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
