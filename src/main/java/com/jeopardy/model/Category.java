package com.jeopardy.model;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a single Jeopardy category containing a set of questions.
 * <p>
 * Questions in a category are keyed by their point value and are expected
 * to all share the same category name.
 */

public class Category {

    /** Display name of this category (e.g. "Science", "History"). */
    private String name;

    /**
     * Map of question value to {@link Question} for this category.
     * <p>
     * A {@link TreeMap} is used so that questions are stored in ascending
     * order of their point value.
     */
    private Map<Integer, Question> questions; 
    
    /**
     * Creates a new category with the given name and an initially empty
     * set of questions.
     *
     * @param name the display name of the category
     */
    public Category(String name) {
        this.name = name;
        this.questions = new TreeMap<>(); 
    }
    
    /**
     * Adds a question to this category.
     * <p>
     * The question's category name must match this category's name;
     * otherwise an {@link IllegalArgumentException} is thrown.
     *
     * @param question the question to add
     * @throws IllegalArgumentException if the question's category name
     *                                  does not match this category's name
     */
    public void addQuestion(Question question) {
        if (!name.equals(question.getCategory())) {
            throw new IllegalArgumentException("Question category must match category name");
        }
        questions.put(question.getValue(), question);
    }
    
    /**
     * Returns the name of this category.
     *
     * @return the category name
     */
    public String getName() { 
        return name; 
    }
    
    /**
     * Returns a copy of the map of questions in this category, keyed by
     * their point value.
     * <p>
     * The returned map is a defensive copy; modifying it does not affect
     * the internal state of this category.
     *
     * @return a new {@link Map} containing all questions in this category
     */
    public Map<Integer, Question> getQuestion() { 
        return new TreeMap<>(questions); 
    }
    
    /**
     * Returns the question associated with the given point value, or
     * {@code null} if no such question exists.
     *
     * @param value the point value of the question to retrieve
     * @return the {@link Question} with the given value, or {@code null}
     *         if none exists
     */
    public Question getQuestions(int value) { 
        return questions.get(value); 
    }
    
    /**
     * Determines whether this category contains a question with the
     * specified point value.
     *
     * @param value the point value to check
     * @return {@code true} if a question with the given value exists;
     *         {@code false} otherwise
     */
    public boolean hasQuestion(int value) { 
        return questions.containsKey(value); 
    }
    
    /**
     * Returns a view of all questions in this category.
     * <p>
     * The returned collection is backed by the internal map, so updates
     * to the map are reflected in the collection.
     *
     * @return a collection of all {@link Question} objects in this category
     */
    public Collection<Question> getAllQuestions() {
        return questions.values();
    }
    
    /**
     * Checks whether all questions in this category have been answered.
     *
     * @return {@code true} if every question is marked as answered;
     *         {@code false} otherwise
     */
    public boolean allQuestionsAnswered() {
        return questions.values().stream().allMatch(Question::isAnswered);
    }
    
    /**
     * Returns the number of unanswered questions in this category.
     *
     * @return the count of questions that are not yet answered
     */
    public int getAvailableQuestionCount() {
        return (int) questions.values().stream().filter(q -> !q.isAnswered()).count();
    }
    
    /**
     * Determines whether there is at least one unanswered question in
     * this category.
     *
     * @return {@code true} if any question remains unanswered;
     *         {@code false} otherwise
     */
    public boolean hasAvailableQuestions() {
        return questions.values().stream().anyMatch(q -> !q.isAnswered());
    }
    
    /**
     * Resets the answered state of all questions in this category so that
     * they are all marked as unanswered.
     */
    public void resetAllQuestions() {
        questions.values().forEach(q -> q.setAnswered(false));
    }
    
    /**
     * Returns a string representation of this category, including its name
     * and the number of questions it contains.
     *
     * @return a string describing this category
     */
    @Override
    public String toString() {
        return String.format("Category{name='%s', questionCount=%d}", name, questions.size());
    }
}