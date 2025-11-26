package com.jeopardy.integration;

import com.jeopardy.model.*;
import com.jeopardy.service.GameController;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests CSV-based event logging as performed by CsvGameEventLogger through
 * the public APIs exposed on {@link GameController}.
 *
 * <p>These tests ensure that:</p>
 * <ul>
 *     <li>A log file is created when the first event is recorded</li>
 *     <li>The CSV header row is accurate</li>
 *     <li>Answer events and system events are written correctly</li>
 *     <li>Duplicate events are not logged for repeated actions</li>
 *     <li>Events appear in the correct chronological order</li>
 * </ul>
 */

public class LoggingTests {

    /** Controller used for invoking gameplay and system events. */
    private GameController controller;

    /** In-memory dataset used to supply categories and questions. */
    private GameData data;

    /** Path where the event CSV log should be written during tests. */
    private static final Path LOG_FILE = Paths.get("logs", "game_event_log.csv");

    /**
     * Prepares a clean test environment:
     * <ul>
     *     <li>Removes any previously generated log file</li>
     *     <li>Initializes {@link GameController} and {@link GameData}</li>
     *     <li>Creates a single Science category with one question</li>
     *     <li>Starts a game with two players: Alice and Bob</li>
     * </ul>
     *
     * @throws IOException if clearing the log or preparing directories fails
     */

    @BeforeEach
    void setup() throws IOException {

        // Delete any previous log file so tests start clean
        if (Files.exists(LOG_FILE)) {
            Files.delete(LOG_FILE);
        }
        Files.createDirectories(LOG_FILE.getParent());

        controller = new GameController();
        data = new GameData();

        // Build categories & questions
        Map<String, String> opts = Map.of(
                "A", "Correct",
                "B", "Wrong",
                "C", "Wrong",
                "D", "Wrong"
        );

        Category sci = new Category("Science");
        sci.addQuestion(new Question("Science", 100, "What is H2O?", opts, "A"));
        data.addCategory(sci);

        controller.initializeGame(List.of("Alice", "Bob"), data);
    }

    /**
     * Ensures that the log file is created immediately after the first
     * gameplay event is recorded (answering a question).
     */

    @Test
    void logFileIsCreatedAfterFirstEvent() throws IOException {
        controller.answerQuestion("Science", 100, "A");

        assertTrue(Files.exists(LOG_FILE), "The CSV log file must exist after logging events");
    }

    /**
     * Validates that the generated log file contains the correct CSV header
     * on its first line.
     */
    @Test
    void logFileContainsCorrectHeader() throws IOException {
        controller.answerQuestion("Science", 100, "A");

        List<String> lines = Files.readAllLines(LOG_FILE);
        assertFalse(lines.isEmpty());

        assertEquals(
                "Case_ID,Player_ID,Activity,Timestamp,Category,Question_Value,Answer_Given,Result,Score_After_Play",
                lines.get(0)
        );
    }

    /**
     * Confirms that a correctly answered question generates a complete and
     * accurately formatted log entry containing all expected fields.
     */
    @Test
    void answerQuestionEventIsLoggedCorrectly() throws IOException {
        controller.answerQuestion("Science", 100, "A");

        List<String> lines = Files.readAllLines(LOG_FILE);
        assertTrue(lines.size() >= 2);

        String[] cols = lines.get(1).split(",", -1);

        assertEquals(9, cols.length);

        assertEquals(controller.getCaseId(), cols[0]);  
        assertEquals("Alice", cols[1]);                
        assertEquals("Answer Question", cols[2]);       
        assertFalse(cols[3].isBlank());                 
        assertEquals("Science", cols[4]);               
        assertEquals("100", cols[5]);                   
        assertEquals("Correct", cols[6]);               
        assertEquals("Correct", cols[7]);               
        assertEquals("100", cols[8]);                   
    }

    /**
     * Ensures that when multiple events occur, they are logged in the
     * correct chronological order (Answer → SystemEvent).
     */
    @Test
    void multipleEventsLoggedInCorrectOrder() throws IOException {
        controller.answerQuestion("Science", 100, "A");
        controller.nextPlayer();
        controller.systemEvent("Start Game", null, null, "Success");

        List<String> lines = Files.readAllLines(LOG_FILE);
        assertTrue(lines.size() >= 3);

        // First logged event after header must be Answer Question
        assertTrue(lines.get(1).contains("Answer Question"));
        // Second event should be system event
        assertTrue(lines.get(2).contains("Start Game"));
    }

    /**
     * Confirms that answering an already-answered question does not
     * produce additional log entries, preventing duplicate events.
     */
    @Test
    void secondAttemptOnAnsweredQuestionDoesNotCreateDuplicateEvents() throws IOException {
        controller.answerQuestion("Science", 100, "A");
        controller.answerQuestion("Science", 100, "A"); // should NOT produce new event

        List<String> lines = Files.readAllLines(LOG_FILE);

        assertEquals(2, lines.size(), "Only one answer event + header expected");
    }

    /**
     * Verifies that explicit system events (not tied to a player) are
     * logged correctly, including proper Player_ID and Result fields.
     */
    @Test
    void systemEventIsLoggedCorrectly() throws IOException {
        controller.systemEvent("Start Game", null, null, "Success");

        List<String> lines = Files.readAllLines(LOG_FILE);

        String[] cols = lines.get(1).split(",", -1);

        assertEquals("System", cols[1]);          
        assertEquals("Start Game", cols[2]);      
        assertEquals("Success", cols[7]);         
    }
}
