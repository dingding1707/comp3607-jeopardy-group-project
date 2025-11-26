package com.jeopardy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the complete Jeopardy dataset, consisting of multiple
 * {@link Category} objects, each containing their own set of questions.
 * <p>
 * This class provides utility methods for adding questions/categories,
 * searching by name, and retrieving global statistics such as total
 * categories and total questions.
 */
public class GameData {

    /** Internal list of all categories in this dataset. */
    private List<Category> categories;

    /**
     * Creates an empty {@code GameData} object with no categories.
     */
    public GameData() {
        this.categories = new ArrayList<>();
    }

    /**
     * Adds a question to the dataset. If a category matching the question’s
     * category name already exists, the question is appended to it. Otherwise,
     * a new category is created automatically.
     *
     * @param question the question to add
     * @throws IllegalArgumentException if the question is {@code null}
     */
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

    /**
     * Adds a new category to the dataset.
     *
     * @param category the category to add
     * @throws IllegalArgumentException if the provided category is {@code null}
     */
    public void addCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        categories.add(category);
    }

    /**
     * Returns a defensive copy of all categories.
     *
     * @return a new {@link List} containing all categories
     */
    public List<Category> getCategories() {
        return new ArrayList<>(categories); 
    }

    /**
     * Retrieves a category by its name, using case-insensitive comparison.
     *
     * @param name the category name to search for
     * @return the matching {@link Category}, or {@code null} if none exists
     */
    public Category getCategory(String name) {
        return categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Searches for a category by name and returns an {@link Optional} result.
     *
     * @param name the category name to search for
     * @return an {@link Optional} containing the found category, or empty if not found
     */
    public Optional<Category> findCategory(String name) {
        return categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Returns the number of categories in this dataset.
     *
     * @return the total category count
     */
    public int getTotalCategories() {
        return categories.size();
    }

    /**
     * Returns the total number of questions across all categories.
     *
     * @return the total question count
     */
    public int getTotalQuestions() {
        return categories.stream()
                .mapToInt(c -> c.getAllQuestions().size())
                .sum();
    }

    /**
     * Checks whether this dataset contains zero categories.
     *
     * @return {@code true} if no categories exist, otherwise {@code false}
     */
    public boolean isEmpty() {
        return categories.isEmpty();
    }

    /**
     * Determines whether a category with the given name exists, using
     * case-insensitive comparison.
     *
     * @param name the category name to check
     * @return {@code true} if a matching category exists, {@code false} otherwise
     */
    public boolean hasCategory(String name) {
        return categories.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }    @Override

    /**
     * Returns a readable summary containing the number of categories
     * and total questions contained.
     *
     * @return a formatted string summarizing this dataset
     */
    public String toString() {
        return String.format(
            "GameData{categories=%d, totalQuestions=%d}",
            getTotalCategories(), getTotalQuestions()
        );
    }
}
