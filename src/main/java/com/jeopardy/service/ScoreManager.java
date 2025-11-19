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
    
    /**
     * Set the scoring strategy dynamically
     */
    public void setScoringStrategy(ScoringStrategy strategy) {
        if (strategy != null) {
            this.scoringStrategy = strategy;
        }
    }
    
    /**
     * Get the current scoring strategy
     */
    public ScoringStrategy getScoringStrategy() {
        return scoringStrategy;
    }
    
    /**
     * Update a player's score based on their answer
     */
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
    
    /**
     * Calculate what the score change would be without applying it
     */
    public int calculatePotentialScore(int questionValue, boolean isCorrect) {
        return scoringStrategy.calculateScore(questionValue, isCorrect);
    }
    
    /**
     * Get the name of the current scoring strategy
     */
    public String getScoringStrategyName() {
        return scoringStrategy.getStrategyName();
    }
}