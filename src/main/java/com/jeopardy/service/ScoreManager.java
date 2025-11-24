package com.jeopardy.service;

import com.jeopardy.model.Player;

/**
 * Manages player scoring using the Strategy pattern
 */

public class ScoreManager {
    private ScoringStrategy scoringStrategy;
    
    public ScoreManager() {
        this.scoringStrategy = new StandardScoringStrategy(); // Default strategy
    }
    
    public ScoreManager(ScoringStrategy scoringStrategy) {
        this.scoringStrategy = scoringStrategy;
    }
    
    /** Sets the scoring strategy. */
    public void setScoringStrategy(ScoringStrategy strategy) {
        if (strategy != null) {
            this.scoringStrategy = strategy;
        }
    }
    
    /** Gets the current strategy. */
    public ScoringStrategy getScoringStrategy() {
        return scoringStrategy;
    }
    
    /** Updates a player's score. */
    public void updateScore(Player player, int questionValue, boolean isCorrect) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        int points = scoringStrategy.calculateScore(questionValue, isCorrect);
        
        if (points > 0) {
            player.addPoints(points);
        } else if (points < 0) {
            player.subtractPoints(Math.abs(points));
        }
        // If points == 0, no change
    }
    
    /** Calculates potential score without applying it. */
    public int calculatePotentialScore(int questionValue, boolean isCorrect) {
        return scoringStrategy.calculateScore(questionValue, isCorrect);
    }
    
    /** Gets the strategy name. */
    public String getScoringStrategyName() {
        return scoringStrategy.getStrategyName();
    }
}