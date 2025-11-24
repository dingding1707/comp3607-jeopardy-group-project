package com.jeopardy.service;

/** Interface for different scoring algorithms. */
public interface ScoringStrategy {
    
    int calculateScore(int questionValue, boolean isCorrect);
    String getStrategyName();
}