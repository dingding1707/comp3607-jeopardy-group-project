package com.jeopardy.integration;

import com.jeopardy.model.*;
import com.jeopardy.service.*;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full system integration test:
 * <ul>
 *   <li>Builds {@link GameData} manually (simulating parsed data)</li>
 *   <li>Initializes the {@link GameController}</li>
 *   <li>Runs a short game sequence</li>
 *   <li>Validates event logging to file</li>
 *   <li>Generates a summary report</li>
 *   <li>Verifies final winners and ending state</li>
 * </ul>
 *
 * <p>This suite ensures that the full Jeopardy system behaves correctly
 * when all components interact together.</p>
 */

public class FullIntegrationTest {

    private GameController controller;
    private GameData data;

    /** Path to the CSV log file where game events should be written. */
    private static final Path LOG_FILE = Paths.get("logs", "game_event_log.csv");

    /**
     * Creates a clean test environment before each test:
     * <ul>
     *   <li>Clears existing log files</li>
     *   <li>Initializes a fresh {@link GameController} and {@link GameData}</li>
     *   <li>Builds a sample dataset with 2 categories and 4 questions</li>
     *   <li>Starts a new game with two players: Alice and Bob</li>
     * </ul>
     *
     * @throws IOException if log file cleanup or directory creation fails
     */

    @BeforeEach
    void setup() throws IOException {

        if (Files.exists(LOG_FILE)) {
            Files.delete(LOG_FILE);
        }
        Files.createDirectories(LOG_FILE.getParent());

        controller = new GameController();
        data = new GameData();

        Map<String, String> opts = Map.of(
                "A", "Correct",
                "B", "Wrong",
                "C", "Wrong",
                "D", "Wrong"
        );

        Category science = new Category("Science");
        science.addQuestion(new Question("Science", 100, "What is H2O?", opts, "A"));
        science.addQuestion(new Question("Science", 200, "Boiling point of water?", opts, "A"));

        Category math = new Category("Math");
        math.addQuestion(new Question("Math", 100, "2 + 2?", opts, "A"));
        math.addQuestion(new Question("Math", 200, "Square root of 16?", opts, "A"));

        data.addCategory(science);
        data.addCategory(math);

        controller.initializeGame(List.of("Alice", "Bob"), data);
    }

    // 1. PARSING + INITIALIZATION → GAME IN PROGRESS

    /**
     * Verifies that a game initialized with valid data:
     * <ul>
     *   <li>Creates the correct number of players</li>
     *   <li>Loads the expected categories and questions</li>
     *   <li>Starts with game state set to {@code IN_PROGRESS}</li>
     * </ul>
     */

    @Test
    void gameStartsWithValidData() {
        assertEquals(2, controller.getPlayers().size());
        assertEquals(2, data.getTotalCategories());
        assertEquals(4, data.getTotalQuestions());
        assertEquals("IN_PROGRESS", controller.getGameState().getStatus());
    }

    // 2. RUN A SHORT GAME SEQUENCE

    /**
     * Runs a short multi-turn gameplay sequence and verifies:
     * <ul>
     *   <li>Correct/incorrect answer scoring</li>
     *   <li>Event logging to the CSV log file</li>
     *   <li>Winner calculation (Alice should lead)</li>
     *   <li>Summary report generation and content validation</li>
     * </ul>
     *
     * @throws IOException if reading from or checking the log/report files fails
     */

    @Test
    void fullGameFlowRunsCorrectly() throws IOException {
        assertTrue(controller.answerQuestion("Science", 100, "A"));
        controller.nextPlayer();

        assertFalse(controller.answerQuestion("Math", 100, "B"));
        controller.nextPlayer();

        assertTrue(controller.answerQuestion("Math", 200, "A"));

        // LOG FILE MUST CONTAIN ALL EVENTS
        List<String> lines = Files.readAllLines(LOG_FILE);
        assertTrue(lines.size() >= 4, "Header + 3 answer events expected");

        String[] cols = lines.get(1).split(",", -1);
        assertEquals("Answer Question", cols[2]);

        List<Player> winners = controller.getWinners();
        assertEquals(1, winners.size());
        assertEquals("Alice", winners.get(0).getName());

        // GENERATE SUMMARY REPORT
        Path report = controller.generateSummaryReport();
        assertTrue(Files.exists(report));

        String rep = Files.readString(report);

        assertTrue(rep.contains("JEOPARDY PROGRAMMING GAME REPORT"));
        assertTrue(rep.contains("Players: Alice, Bob"));
        assertTrue(rep.contains("Science")); // category names
        assertTrue(rep.contains("Math"));
        assertTrue(rep.contains("Final Scores"));
        assertTrue(rep.contains("Alice:"));
        assertTrue(rep.contains("Bob:"));
    }

    // 3. FULL GAME END CONDITION

    /**
     * Ensures the game correctly transitions to the {@code FINISHED} state
     * once all questions in all categories have been answered.
     * <p>
     * Also validates:
     * <ul>
     *   <li>{@link GameController#checkAndEndGame()} returns true</li>
     *   <li>{@link GameController#isGameFinished()} becomes true</li>
     *   <li>GameState status updates to {@code FINISHED}</li>
     * </ul>
     */

    @Test
    void gameEndsWhenAllQuestionsAnswered() {
        controller.answerQuestion("Science", 100, "A");
        controller.answerQuestion("Science", 200, "A");
        controller.answerQuestion("Math", 100, "A");
        controller.answerQuestion("Math", 200, "A");

        boolean ended = controller.checkAndEndGame();

        assertTrue(ended);
        assertTrue(controller.isGameFinished());
        assertEquals("FINISHED", controller.getGameState().getStatus());
    }
}
