package com.jeopardy.service;

/**
 * Strategy pattern interface for different scoring algorithms
 */
public interface ScoringStrategy {
    
    /**
     * Calculate the score change based on question value and answer correctness
     * @param questionValue the point value of the question
     * @param isCorrect whether the answer was correct
     * @return the points to add (positive) or subtract (negative)
     */
    int calculateScore(int questionValue, boolean isCorrect);
    
    /**
     * Get the name of the scoring strategy for display purposes
     * @return strategy name
     */
    String getStrategyName();
}