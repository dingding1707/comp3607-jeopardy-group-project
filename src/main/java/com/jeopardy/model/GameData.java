package com.jeopardy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Represents the complete set of game data with categories and questions. */
public class GameData {

    private List<Category> categories;

    public GameData() {
        this.categories = new ArrayList<>();
    }

    public void addQuestion(Question question) {
        String categoryName = question.getCategory();

        
        Optional<Category> existing = findCategory(categoryName);

        if (existing.isPresent()) {
            existing.get().addQuestion(question);
        } else {
            
            Category newCategory = new Category(categoryName);
            newCategory.addQuestion(question);
            categories.add(newCategory);
        }
    }

    /** Adds a category to the game data. */
    public void addCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        categories.add(category);
    }

    /** Returns a copy of all categories. */
    public List<Category> getCategories() {
        return new ArrayList<>(categories); 
    }

    /** Gets a category by name (case-insensitive). */
    public Category getCategory(String name) {
        return categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /** Finds a category by name using Optional. */
    public Optional<Category> findCategory(String name) {
        return categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public int getTotalCategories() {
        return categories.size();
    }

    public int getTotalQuestions() {
        return categories.stream()
                .mapToInt(c -> c.getAllQuestions().size())
                .sum();
    }

    public boolean isEmpty() {
        return categories.isEmpty();
    }

    /** Checks if a category exists by name (case-insensitive). */
    public boolean hasCategory(String name) {
        return categories.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }    @Override
    public String toString() {
        return String.format(
            "GameData{categories=%d, totalQuestions=%d}",
            getTotalCategories(), getTotalQuestions()
        );
    }
}
