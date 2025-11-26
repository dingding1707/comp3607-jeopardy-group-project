package com.jeopardy.integration;

import com.jeopardy.model.*;
import com.jeopardy.service.GameController;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests summary report generation produced through the
 * {@link GameController}'s summary report API (e.g. TextSummaryReportGenerator).
 *
 * <p>These tests verify that the report:</p>
 * <ul>
 *     <li>is created at the expected location</li>
 *     <li>includes the case ID and player list</li>
 *     <li>contains turn-by-turn gameplay details</li>
 *     <li>shows final scores correctly</li>
 *     <li>handles ties with an appropriate message</li>
 * </ul>
 *
 * Fully aligned with the exact formatting rules inside {@code generate()}.
 */

public class ReportingTests {

    /** Game controller used to simulate gameplay and generate reports. */
    private GameController controller;

    /** Simple science category used as a controlled data set. */
    private Category sci;

    /** In-memory game data containing the category and question. */
    private GameData data;

    /** Path to the generated report file for each test. */
    private Path reportPath;

    /**
     * Sets up a fresh game and a clean report directory before each test.
     * <p>
     * Creates a single Science category with one question and initializes
     * a game with two players, Alice and Bob. It also ensures the
     * {@code report/} directory is cleared of any previous report files.
     *
     * @throws IOException if any file or directory operation fails
     */
    @BeforeEach
    void setup() throws IOException {
        controller = new GameController();
        data = new GameData();

        // Build simple category with 1 question for controlled testing
        Map<String, String> opts = Map.of(
                "A", "Correct",
                "B", "Wrong",
                "C", "Wrong",
                "D", "Wrong"
        );

        sci = new Category("Science");
        sci.addQuestion(new Question("Science", 100, "What is H2O?", opts, "A"));
        data.addCategory(sci);

        controller.initializeGame(List.of("Alice", "Bob"), data);

        // Ensure clean report directory
        Path reportDir = Paths.get("report");
        if (Files.exists(reportDir)) {
            Files.walk(reportDir).filter(Files::isRegularFile).forEach(f -> {
                try { Files.deleteIfExists(f); } catch (IOException ignored) {}
            });
        }
    }

    // 1. REPORT FILE IS GENERATED

    /**
     * Verifies that generating a summary report produces a non-null
     * path and that the corresponding file exists with the expected
     * filename {@code summary_report.txt}.
     *
     * @throws IOException if reading or creating the report file fails
     */
    @Test
    void reportFileIsCreated() throws IOException {
        controller.answerQuestion("Science", 100, "A");
        reportPath = controller.generateSummaryReport();

        assertNotNull(reportPath);
        assertTrue(Files.exists(reportPath), "Report file must exist");
        assertTrue(reportPath.getFileName().toString().equals("summary_report.txt"));
    }

    // 2. REPORT CONTAINS CASE ID

    /**
     * Ensures that the generated report includes the unique case ID
     * associated with the current game.
     *
     * @throws IOException if the report file cannot be read
     */
    @Test
    void reportContainsCaseId() throws IOException {
        controller.answerQuestion("Science", 100, "A");
        reportPath = controller.generateSummaryReport();

        String text = Files.readString(reportPath);
        assertTrue(text.contains("Case ID: " + controller.getCaseId()));
    }

    // 3. CONTAINS PLAYER LIST

    /**
     * Verifies that the report lists all players in the game in the
     * expected format (e.g. {@code Players: Alice, Bob}).
     *
     * @throws IOException if the report file cannot be read
     */
    @Test
    void reportListsPlayers() throws IOException {
        controller.answerQuestion("Science", 100, "A");
        reportPath = controller.generateSummaryReport();

        String text = Files.readString(reportPath);
        assertTrue(text.contains("Players: Alice, Bob"));
    }

    // 4. TURN-BY-TURN SUMMARY IS CORRECT

    /**
     * Confirms that the report contains a detailed gameplay summary,
     * including the turn number, selected category and value, question
     * text, answer result and score after the turn.
     *
     * @throws IOException if the report file cannot be read
     */
    @Test
    void reportContainsTurnSummary() throws IOException {
        controller.answerQuestion("Science", 100, "A");

        reportPath = controller.generateSummaryReport();
        String text = Files.readString(reportPath);

        assertTrue(text.contains("Gameplay Summary:"));
        assertTrue(text.contains("Turn 1: Alice selected Science for 100 pts"));
        assertTrue(text.contains("Question: What is H2O?"));
        assertTrue(text.contains("Answer: Correct — Correct (+100 pts)"));
        assertTrue(text.contains("Score after turn: Alice = 100"));
    }

    // 5. FINAL SCORES SECTION

    /**
     * Ensures that the report shows a final scores section and that
     * each player's ending score is correctly displayed.
     *
     * @throws IOException if the report file cannot be read
     */
    @Test
    void reportShowsFinalScores() throws IOException {
        controller.answerQuestion("Science", 100, "A");

        reportPath = controller.generateSummaryReport();
        String text = Files.readString(reportPath);

        assertTrue(text.contains("Final Scores:"));
        assertTrue(text.contains("Alice: 100"));
        assertTrue(text.contains("Bob: 0"));
    }

    // 6. TIE MESSAGE WHEN MULTIPLE WINNERS

    /**
     * Verifies that when multiple players share the highest score,
     * the report includes a tie message listing all winners with
     * their corresponding points.
     *
     * @throws IOException if the report file cannot be read
     */
    @Test
    void reportShowsTieMessage() throws IOException {
        // Make both players equal score
        controller.answerQuestion("Science", 100, "A"); // Alice +100
        controller.nextPlayer();
        controller.getCurrentPlayer().addPoints(100);   // Bob manual tie

        reportPath = controller.generateSummaryReport();
        String text = Files.readString(reportPath);

        assertTrue(text.contains("It's a tie! Winners:"), "Tie message must be present when scores equal");
        assertTrue(text.contains("Alice (100 points)"));
        assertTrue(text.contains("Bob (100 points)"));
    }
}
