package com.jeopardy.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a multiple-choice question in the Jeopardy game.
 * <p>
 * Each question belongs to a category, has a point value, text,
 * four options (A–D), and one correct answer. Questions can also
 * be marked as answered to prevent reuse.
 */
public class Question {
    /** Category name this question belongs to (e.g. "Science"). */
    private String category;

    /** Point value of this question (must be positive). */
    private int value;

    /** The text of the question shown to players. */
    private String questionText;

    /** Map of choice labels (A–D) to option text. */
    private Map<String, String> options;

    /** The correct choice label (A, B, C or D). */
    private String correctAnswer;

    /** Flag indicating whether this question has already been answered. */
    private boolean isAnswered;
    
    /**
     * Creates a question with the specified parameters.
     * <p>
     * A defensive copy of the options map is stored, and the correct answer
     * key is normalized to upper case. Validation is performed to ensure:
     * <ul>
     *     <li>value &gt; 0</li>
     *     <li>options contain A, B, C and D</li>
     *     <li>correctAnswer is one of A, B, C or D</li>
     * </ul>
     *
     * @param category      the category name this question belongs to
     * @param value         the point value of the question (must be positive)
     * @param questionText  the question text
     * @param options       map of option key (A–D) to option text
     * @param correctAnswer the correct option key (A, B, C or D)
     * @throws IllegalArgumentException if any validation rule is violated
     */
    public Question(String category, int value, String questionText, 
                   Map<String, String> options, String correctAnswer) {
        this.category = category;
        this.value = value;
        this.questionText = questionText;
        this.options = new HashMap<>(options); 
        this.correctAnswer = correctAnswer.toUpperCase(); 
        this.isAnswered = false;
        
        validateQuestion();
    }
    
    /**
     * Validates the internal state of this question.
     * <p>
     * Ensures that:
     * <ul>
     *     <li>The value is positive</li>
     *     <li>All four options A, B, C and D are present</li>
     *     <li>The correct answer key refers to an existing option</li>
     * </ul>
     *
     * @throws IllegalArgumentException if any validation rule is violated
     */
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
    
    /**
     * Returns the category name this question belongs to.
     *
     * @return the category name
     */
    public String getCategory() { 
        return category; 
    }
    
    /**
     * Returns the point value of this question.
     *
     * @return the question value
     */
    public int getValue() { 
        return value; 
    }

     /**
     * Returns the question text.
     *
     * @return the question text
     */
    public String getQuestionText() { 
        return questionText; 
    }
    
    /**
     * Returns a defensive copy of the options map.
     * <p>
     * Modifying the returned map will not affect the internal state
     * of this question.
     *
     * @return a new map containing all options for this question
     */
    public Map<String, String> getOptions() { 
        return new HashMap<>(options); 
    }
    
    /**
     * Returns the correct answer key (A, B, C or D).
     *
     * @return the correct answer option key
     */
    public String getCorrectAnswer() { 
        return correctAnswer; 
    }
    
    /**
     * Indicates whether this question has already been answered.
     *
     * @return {@code true} if the question has been answered, {@code false} otherwise
     */
    public boolean isAnswered() { 
        return isAnswered; 
    }
    
    /**
     * Sets the answered state of this question.
     *
     * @param answered {@code true} if the question has been answered;
     *                 {@code false} otherwise
     */
    public void setAnswered(boolean answered) { 
        this.isAnswered = answered; 
    }
    
    /**
     * Checks whether the given user answer matches the correct answer.
     * <p>
     * Comparison is case-insensitive and ignores leading/trailing whitespace.
     *
     * @param userAnswer the answer provided by the user (option key or text)
     * @return {@code true} if the answer is correct, {@code false} otherwise
     */
    public boolean isCorrect(String userAnswer) {
        if (userAnswer == null) return false;
        return correctAnswer.equalsIgnoreCase(userAnswer.trim());
    }
    
    /**
     * Returns the full text of the correct answer option.
     *
     * @return the text corresponding to the correct answer key, or {@code null}
     *         if the option is not present
     */
    public String getCorrectAnswerText() {
        return options.get(correctAnswer);
    }
    
    /**
     * Returns the option text associated with the given choice key.
     * <p>
     * The choice is treated in a case-insensitive manner (e.g. "a" and "A"
     * are equivalent).
     *
     * @param choice the option key (A–D)
     * @return the corresponding option text, or {@code null} if not present
     */
    public String getOption(String choice) {
        return options.get(choice.toUpperCase());
    }
    
    /**
     * Returns a string representation of this question, including its
     * category, value, question text and answered state.
     *
     * @return a string describing this question
     */
    @Override
    public String toString() {
        return String.format("Question{category='%s', value=%d, question='%s', answered=%s}", 
                           category, value, questionText, isAnswered);
    }
}