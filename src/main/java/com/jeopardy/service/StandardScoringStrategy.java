package com.jeopardy.service;

/** Standard Jeopardy scoring: +value for correct, -value for incorrect. */

public class StandardScoringStrategy implements ScoringStrategy {
    
    /** Calculates standard Jeopardy scoring. */
    @Override
    public int calculateScore(int questionValue, boolean isCorrect) {
        return isCorrect ? questionValue : -questionValue;
    }
    
    @Override
    public String getStrategyName() {
        return "Standard Scoring";
    }
}