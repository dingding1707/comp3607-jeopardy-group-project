package com.jeopardy.service;

import com.jeopardy.model.Category;
import com.jeopardy.model.Question;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validates categories and questions before the game starts.
 * Used by CSV, JSON, and XML loaders.
 */

public class DataValidator {
    public static void validateCategories(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("No categories found in file.");
        }

        Set<String> categoryNames = new HashSet<>();

        for (Category c : categories) {
            validateCategory(c);

            if (!categoryNames.add(c.getName().toLowerCase())) {
                throw new IllegalArgumentException("Duplicate category name: " + c.getName());
            }
        }
    }

    public static void validateCategory(Category c) {

        if (c.getName() == null || c.getName().isBlank()) {
            throw new IllegalArgumentException("Category name is missing.");
        }

        // getQuestions() returns a MAP <Integer, Question>
        if (c.getAllQuestions() == null || c.getAllQuestions().isEmpty()) {
            throw new IllegalArgumentException(
                "Category '" + c.getName() + "' has no questions."
            );
        }

        Set<Integer> values = new HashSet<>();

        // Iterate through the values() of the map
        for (Question q : c.getAllQuestions()) {

            validateQuestion(q, c.getName());

            if (!values.add(q.getValue())) {
                throw new IllegalArgumentException(
                    "Category '" + c.getName() + "' has repeated question value: " + q.getValue()
                );
            }
        }
    }

    public static void validateQuestion(Question q, String categoryName) {
        if (q.getQuestionText() == null || q.getQuestionText().isBlank()) {
            throw new IllegalArgumentException("A question in category '" + categoryName + "' has no text.");
        }

        Map<String, String> options = q.getOptions();
        if (options == null ||
                !options.containsKey("A") ||
                !options.containsKey("B") ||
                !options.containsKey("C") ||
                !options.containsKey("D")) {
            throw new IllegalArgumentException(
                    "A question in category '" + categoryName + "' must have options A, B, C, and D.");
        }

        for (String key : new String[]{"A", "B", "C", "D"}) {
            String option = options.get(key);
            if (option == null || option.isBlank()) {
                throw new IllegalArgumentException(
                        "Option " + key + " in category '" + categoryName + "' cannot be empty.");
            }
        }

        String correct = q.getCorrectAnswer();
        if (correct == null ||
                !"ABCD".contains(correct.toUpperCase())) {
            throw new IllegalArgumentException(
                    "Invalid correct option in category '" + categoryName +
                            "'. Must be A, B, C, or D.");
        }

        if (q.getValue() <= 0) {
            throw new IllegalArgumentException(
                    "Question in '" + categoryName + "' has invalid value: " + q.getValue());
        }
    }
}
