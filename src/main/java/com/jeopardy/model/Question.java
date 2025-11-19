package com.jeopardy.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Jeopardy question with multiple choice options
 */
public class Question {
    private String category;
    private int value;
    private String questionText;
    private Map<String, String> options; // A, B, C, D
    private String correctAnswer; // A, B, C, or D
    private boolean isAnswered;
    
    public Question(String category, int value, String questionText, 
                   Map<String, String> options, String correctAnswer) {
        this.category = category;
        this.value = value;
        this.questionText = questionText;
        this.options = new HashMap<>(options); // Defensive copy
        this.correctAnswer = correctAnswer.toUpperCase(); // Ensure uppercase
        this.isAnswered = false;
        
        validateQuestion();
    }
    
    private void validateQuestion() {
        if (value <= 0) {
            throw new IllegalArgumentException("Question value must be positive");
        }
        if (!options.containsKey("A") || !options.containsKey("B") || 
            !options.containsKey("C") || !options.containsKey("D")) {
            throw new IllegalArgumentException("Question must have options A, B, C, and D");
        }
        if (!options.containsKey(correctAnswer)) {
            throw new IllegalArgumentException("Correct answer must be one of the options A, B, C, D");
        }
    }
    
    // Getters
    public String getCategory() { 
        return category; 
    }
    
    public int getValue() { 
        return value; 
    }
    
    public String getQuestionText() { 
        return questionText; 
    }
    
    public Map<String, String> getOptions() { 
        return new HashMap<>(options); // Return defensive copy
    }
    
    public String getCorrectAnswer() { 
        return correctAnswer; 
    }
    
    public boolean isAnswered() { 
        return isAnswered; 
    }
    
    // Setters
    public void setAnswered(boolean answered) { 
        this.isAnswered = answered; 
    }
    
    // Business methods
    public boolean isCorrect(String userAnswer) {
        if (userAnswer == null) return false;
        return correctAnswer.equalsIgnoreCase(userAnswer.trim());
    }
    
    public String getCorrectAnswerText() {
        return options.get(correctAnswer);
    }
    
    public String getOption(String choice) {
        return options.get(choice.toUpperCase());
    }
    
    @Override
    public String toString() {
        return String.format("Question{category='%s', value=%d, question='%s', answered=%s}", 
                           category, value, questionText, isAnswered);
    }
}