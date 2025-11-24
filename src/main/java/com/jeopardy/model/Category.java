package com.jeopardy.model;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/** Represents a Jeopardy category containing questions. */
public class Category {
    private String name;
    private Map<Integer, Question> questions; 
    
    /** Creates a category with the given name. */
    public Category(String name) {
        this.name = name;
        this.questions = new TreeMap<>(); 
    }
    
    /** Adds a question to this category. */
    public void addQuestion(Question question) {
        if (!name.equals(question.getCategory())) {
            throw new IllegalArgumentException("Question category must match category name");
        }
        questions.put(question.getValue(), question);
    }
    
    /** Gets the category name. */
    public String getName() { 
        return name; 
    }
    
    /** Gets a copy of all questions in this category. */
    public Map<Integer, Question> getQuestion() { 
        return new TreeMap<>(questions); 
    }
    
    /** Gets a question by its point value. */
    public Question getQuestions(int value) { 
        return questions.get(value); 
    }
    
    /** Checks if a question with the given value exists. */
    public boolean hasQuestion(int value) { 
        return questions.containsKey(value); 
    }
    
    /** Returns all questions in this category. */
    public Collection<Question> getAllQuestions() {
        return questions.values();
    }
    
    /** Checks if all questions in this category are answered. */
    public boolean allQuestionsAnswered() {
        return questions.values().stream().allMatch(Question::isAnswered);
    }
    
    /** Returns the count of unanswered questions. */
    public int getAvailableQuestionCount() {
        return (int) questions.values().stream().filter(q -> !q.isAnswered()).count();
    }
    
    /** Checks if there are any unanswered questions. */
    public boolean hasAvailableQuestions() {
        return questions.values().stream().anyMatch(q -> !q.isAnswered());
    }
    
    /** Resets all questions to unanswered. */
    public void resetAllQuestions() {
        questions.values().forEach(q -> q.setAnswered(false));
    }
    
    @Override
    public String toString() {
        return String.format("Category{name='%s', questionCount=%d}", name, questions.size());
    }
}