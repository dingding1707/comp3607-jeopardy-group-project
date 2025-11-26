package com.jeopardy.integration;

import com.jeopardy.model.Player;
import com.jeopardy.service.ScoreManager;
import com.jeopardy.service.ScoringStrategy;
import com.jeopardy.service.StandardScoringStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the scoring system independently of gameplay.
 * <p>
 * Covers:
 * <ul>
 *     <li>Standard scoring strategy behavior</li>
 *     <li>{@link ScoreManager} score update logic</li>
 *     <li>Non-negative score enforcement</li>
 *     <li>{@link ScoreManager#calculatePotentialScore(int, boolean)} logic</li>
 *     <li>Switching scoring strategies via the Strategy pattern</li>
 * </ul>
 */
public class ScoringTests {

    /** Manager responsible for updating scores using a configured strategy. */
    private ScoreManager scoreManager;

    /** Test player used across scoring scenarios. */
    private Player p;

    /**
     * Initializes a fresh {@link ScoreManager} (with the default
     * {@link StandardScoringStrategy}) and a single player before each test.
     */
    @BeforeEach
    void setup() {
        scoreManager = new ScoreManager(); // Uses StandardScoringStrategy by default
        p = new Player("P1", "Alice");
    }

    // 1. CORRECT ANSWER → +value

    /**
     * Verifies that a correct answer increases the player's score
     * by the question value when using the standard scoring strategy.
     */
    @Test
    void correctAnswerAddsPoints() {
        int before = p.getScore();

        scoreManager.updateScore(p, 200, true);

        assertEquals(before + 200, p.getScore());
    }

    // 2. INCORRECT ANSWER → -value (but not below zero)
    /**
     * Ensures that an incorrect answer subtracts the question value
     * from the player's score under the standard scoring strategy.
     */
    @Test
    void wrongAnswerSubtractsPoints() {
        p.setScore(300);

        scoreManager.updateScore(p, 200, false);

        assertEquals(100, p.getScore());
    }

    /**
     * Confirms that the score is never allowed to drop below zero,
     * even when the penalty would exceed the current score.
     */
    @Test
    void scoreDoesNotGoNegative() {
        p.setScore(100);

        scoreManager.updateScore(p, 200, false);

        assertEquals(0, p.getScore(), "Score must never go below zero");
    }

    // 3. POTENTIAL SCORE CALCULATION

    /**
     * Tests {@link ScoreManager#calculatePotentialScore(int, boolean)} to ensure
     * it returns the correct potential delta for both correct and incorrect outcomes.
     */
    @Test
    void calculatePotentialScoreWorks() {
        int potentialCorrect = scoreManager.calculatePotentialScore(300, true);
        int potentialWrong = scoreManager.calculatePotentialScore(300, false);

        assertEquals(300, potentialCorrect);
        assertEquals(-300, potentialWrong);
    }

    // 4. STRATEGY PATTERN SUPPORT

    /**
     * Verifies that {@link ScoreManager} uses the injected {@link ScoringStrategy}
     * implementation and that switching strategies at runtime changes scoring behavior.
     */
    @Test
    void scoreManagerUsesInjectedStrategy() {

        // Custom mock strategy for testing
        ScoringStrategy doubleStrategy = new ScoringStrategy() {
            @Override
            public int calculateScore(int value, boolean correct) {
                if (correct) return value * 2;
                return -50;
            }

            @Override
            public String getStrategyName() {
                return "DoublePoints";
            }
        };

        scoreManager.setScoringStrategy(doubleStrategy);

        p.setScore(0);

        // Correct answer should give double
        scoreManager.updateScore(p, 100, true);
        assertEquals(200, p.getScore());

        // Wrong answer should subtract 50 (but not below zero)
        scoreManager.updateScore(p, 100, false);
        assertEquals(150, p.getScore());

        assertEquals("DoublePoints", scoreManager.getScoringStrategyName());
    }

    // 5. STANDARD STRATEGY NAME CHECK

    /**
     * Ensures that the standard scoring strategy reports its name
     * correctly via {@link StandardScoringStrategy#getStrategyName()}.
     */
    @Test
    void standardStrategyReportsCorrectName() {
        StandardScoringStrategy strat = new StandardScoringStrategy();
        assertEquals("Standard Scoring", strat.getStrategyName());
    }
}
