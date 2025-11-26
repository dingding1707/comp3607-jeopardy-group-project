package com.jeopardy.integration;

import com.jeopardy.model.*;
import com.jeopardy.service.GameController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style gameplay tests that exercise:
 * - Player initialization and turn rotation
 * - Question selection from categories
 * - Correct and incorrect scoring behaviour
 * - Prevention of answering the same question twice
 * - Automatic game end when all questions are answered
 *
 * These tests are aligned with GameController, GameState, Player,
 * Category and Question.
 */

public class GameplayLogicTests {

    /** The game controller used in each test to drive gameplay logic. */
    private GameController controller;

    /** In-memory game data containing categories and questions for testing. */
    private GameData data;

    /** Science category used across multiple gameplay scenarios. */
    private Category science;

    /** Math category used across multiple gameplay scenarios. */
    private Category math;

    /**
     * Sets up a fresh game environment before each test:
     * <ul>
     *     <li>Initializes a new {@link GameController} and {@link GameData}</li>
     *     <li>Creates two categories: Science and Math</li>
     *     <li>Adds two questions per category with deterministic options</li>
     *     <li>Starts a game with players Alice and Bob</li>
     * </ul>
     */
    @BeforeEach
    void setup() {
        controller = new GameController();
        data = new GameData();

        Map<String, String> opts = new HashMap<>();
        opts.put("A", "Correct");
        opts.put("B", "Wrong");
        opts.put("C", "Wrong");
        opts.put("D", "Wrong");

        science = new Category("Science");
        science.addQuestion(new Question("Science", 100, "Science Q1", opts, "A"));
        science.addQuestion(new Question("Science", 200, "Science Q2", opts, "A"));

        math = new Category("Math");
        math.addQuestion(new Question("Math", 100, "Math Q1", opts, "A"));
        math.addQuestion(new Question("Math", 200, "Math Q2", opts, "A"));

        data.addCategory(science);
        data.addCategory(math);

        controller.initializeGame(List.of("Alice", "Bob"), data);
    }

    // 1. TURN ROTATION: Alice → Bob → Alice → Bob

    /**
     * Ensures that player turns rotate correctly in a two-player game.
     * <p>Verifies the sequence: Alice → Bob → Alice.</p>
     */
    @Test
    void turnRotatesCorrectly() {
        Player p1 = controller.getCurrentPlayer();
        assertEquals("Alice", p1.getName());

        controller.nextPlayer();
        assertEquals("Bob", controller.getCurrentPlayer().getName());

        controller.nextPlayer();
        assertEquals("Alice", controller.getCurrentPlayer().getName());
    }

    // 2. SELECTING QUESTIONS

    /**
     * Verifies that selecting a question by category and value returns
     * the expected {@link Question} instance.
     */
    @Test
    void selectingQuestionReturnsCorrectObject() {
        Question q = controller.getQuestion("Science", 100);
        assertNotNull(q);
        assertEquals("Science Q1", q.getQuestionText());
    }

    // 3. PREVENT RESELECTING ANSWERED QUESTIONS

    /**
     * Ensures that once a question has been answered:
     * <ul>
     *     <li>The question is marked as answered</li>
     *     <li>Subsequent attempts to answer the same question return false</li>
     * </ul>
     */
    @Test
    void questionCannotBeAnsweredTwice() {
        assertTrue(controller.answerQuestion("Science", 100, "A"));
        Question q = controller.getQuestion("Science", 100);
        assertTrue(q.isAnswered());

        // Second attempt:
        boolean result = controller.answerQuestion("Science", 100, "A");
        assertFalse(result, "Cannot answer an already answered question");
    }

    // 4. SCORING — Correct Answer

    /**
     * Verifies that answering a question correctly awards points
     * equal to the question's value to the current player.
     */
    @Test
    void correctAnswerAddPoints() {
        Player p = controller.getCurrentPlayer();
        int before = p.getScore();

        boolean correct = controller.answerQuestion("Math", 200, "A");

        assertTrue(correct);
        assertEquals(before + 200, p.getScore());
    }

    // 5. SCORING — Incorrect Answer

    /**
     * Verifies scoring behavior for an incorrect answer:
     * <ul>
     *     <li>The answer is marked incorrect</li>
     *     <li>The player's score does not become negative</li>
     * </ul>
     */
    @Test
    void wrongAnswerSubtractsPoints() {
        Player p = controller.getCurrentPlayer();
        int before = p.getScore();

        boolean correct = controller.answerQuestion("Math", 200, "B");

        assertFalse(correct);
        assertEquals(before, p.getScore() - 0, "Score should subtract but not below zero");
        assertEquals(0, p.getScore(), "Score cannot go below zero");
    }

    // 6. FULL ROUND: Correctness + Turn Rotation

    /**
     * Ensures that after a question is answered and {@link GameController#nextPlayer()}
     * is called, the turn moves to a different player.
     *
     * <p>The answer given depends on the current player's score so that both
     * correct and incorrect flows can be exercised.</p>
     */
    @Test
    void answeringQuestionAdvancesTurn() {
        Player before = controller.getCurrentPlayer();
        controller.answerQuestion("Science", 100, before.getScore() == 0 ? "A" : "B");
        controller.nextPlayer();

        Player after = controller.getCurrentPlayer();
        assertNotEquals(before.getName(), after.getName());
    }

    // 7. END GAME AUTOMATICALLY WHEN ALL QUESTIONS ANSWERED

    /**
     * Confirms that the game detects when all questions have been answered
     * and transitions to a finished state.
     */
    @Test
    void gameEndsWhenAllQuestionsAnswered() {
        // 4 questions total
        controller.answerQuestion("Science", 100, "A");
        controller.answerQuestion("Science", 200, "A");
        controller.answerQuestion("Math", 100, "A");
        controller.answerQuestion("Math", 200, "A");

        boolean ended = controller.checkAndEndGame();

        assertTrue(ended);
        assertTrue(controller.isGameFinished());
    }

    // 8. Cannot answer question from non-existent category

    /**
     * Ensures that attempting to answer a question from a non-existent
     * category or invalid value safely returns {@code false}.
     */
    @Test
    void answeringQuestionFromInvalidCategoryReturnsFalse() {
        boolean result = controller.answerQuestion("INVALID", 500, "A");
        assertFalse(result);
    }

    // 9. getWinners() works correctly

    /**
     * Tests that {@link GameController#getWinners()} correctly identifies
     * the player with the highest score after several turns.
     * <p>Scenario: Alice answers correctly, Bob answers incorrectly; Alice
     * should be the sole winner.</p>
     */
    @Test
    void winnerIsDeterminedCorrectly() {
        controller.answerQuestion("Science", 100, "A"); // Alice +100
        controller.nextPlayer();
        controller.answerQuestion("Math", 100, "B");   // Bob wrong -> stays 0

        List<Player> winners = controller.getWinners();

        assertEquals(1, winners.size());
        assertEquals("Alice", winners.get(0).getName());
    }
}
