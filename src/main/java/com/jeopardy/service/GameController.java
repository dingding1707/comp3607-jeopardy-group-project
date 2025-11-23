package com.jeopardy.service;

import java.util.List;
import java.util.ArrayList;

import com.jeopardy.model.Category;
import com.jeopardy.model.GameData;
import com.jeopardy.model.GameEvent;
import com.jeopardy.model.GameState;
import com.jeopardy.model.Player;
import com.jeopardy.model.Question;

import java.time.Instant;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Main controller that orchestrates the entire game flow
 */
public class GameController {
    private Game game;
    private ScoreManager scoreManager;
    private GameData gameData;

    private GameEventLogger eventLogger;
    private SummaryReportGenerator reportGenerator;

    // Keep all events in memory for the summary report
    private final List<GameEvent> gameEvents = new ArrayList<>();

    public GameController() {
        this(new TextSummaryReportGenerator(), new CsvGameEventLogger());
    }

    public GameController(SummaryReportGenerator reportGenerator,
                          GameEventLogger eventLogger) {
        this.game = new Game();
        this.scoreManager = new ScoreManager();
        this.reportGenerator = reportGenerator;
        this.eventLogger = eventLogger;
    }

    public Game getGame() {
        return game;
    }

    public GameData getGameData() {
        return gameData;
    }

    public GameState getGameState() {
        return game.getState();
    }

    public void initializeGame(List<String> playerNames, GameData gameData) {
        if (playerNames == null || playerNames.isEmpty()) {
            throw new IllegalArgumentException("Player names cannot be null or empty");
        }
        if (gameData == null || gameData.isEmpty()) {
            throw new IllegalArgumentException("Game data cannot be null or empty");
        }

        this.game = new Game();
        this.scoreManager = new ScoreManager();

        for (String name : playerNames) {
            game.addPlayer(name.trim());
        }

        this.gameData = gameData;
        game.setCategories(gameData.getCategories());

        game.startGame();

        systemEvent("Start Game", null, null, null);
    }

    public boolean answerQuestion(String categoryName, int questionValue, String playerAnswer) {
        if (game.getState() != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress");
        }

        Player currentPlayer = game.getCurrentPlayer();
        Question question = game.getQuestion(categoryName, questionValue);

        if (question == null) throw new IllegalArgumentException("Invalid question");
        if (question.isAnswered()) throw new IllegalStateException("Question already answered");

        boolean correct = question.isCorrect(playerAnswer);

        scoreManager.updateScore(currentPlayer, questionValue, correct);
        question.setAnswered(true);

        String fullAnswer = resolveAnswerText(categoryName, questionValue, playerAnswer);

        // Activity name: "Answer Question" (used by both log & summary)
        logEvent("Answer Question",
                currentPlayer,
                categoryName,
                questionValue,
                fullAnswer,
                correct ? "Correct" : "Incorrect",
                question.getQuestionText());

        game.nextTurn();
        return correct;
    }

    public boolean checkAndEndGame() {
        if (!game.hasAvailableQuestions() || game.allQuestionsAnswered()) {
            forceEndGame();
            return true;
        }
        return false;
    }

    public void forceEndGame() {
        if (!isGameFinished()) {
            game.endGame();
            systemEvent("End Game", null, null, null);
        }
    }

    public List<Player> getPlayers() { return game.getPlayers(); }
    public List<Category> getCategories() { return game.getCategories(); }
    public Player getCurrentPlayer() { return game.getCurrentPlayer(); }
    public boolean isGameFinished() { return game.isGameFinished(); }
    public Player getWinner() { return game.getWinner(); }
    public List<GameEvent> getGameEvents() { return List.copyOf(gameEvents); }

    private String resolveAnswerText(String category, Integer value, String letter) {
        if (letter == null) return "";
        if (category == null || value == null) return letter;

        Question q = game.getQuestion(category, value);
        if (q == null) return letter;

        String full = q.getOptions().get(letter);
        return full != null ? full : letter;
    }

    public void systemEvent(String activity, String category, Integer value, String extra) {
        logEvent(activity, null, category, value, extra, null, null);
    }

    // NEW: 6-arg convenience overload
    private void logEvent(String activity,
                          Player player,
                          String category,
                          Integer questionValue,
                          String answerGiven,
                          String result) {
        logEvent(activity, player, category, questionValue, answerGiven, result, null);
    }

    // Main logger with questionText
    private void logEvent(String activity,
                          Player player,
                          String category,
                          Integer questionValue,
                          String answerGiven,
                          String result,
                          String questionText) {

        GameEvent event = new GameEvent.Builder(
                game.getGameId(),
                activity
        )
                .playerId(player != null ? player.getName() : "System")
                .timestamp(Instant.now())
                .category(category)
                .questionValue(questionValue)
                .answerGiven(answerGiven)
                .result(result)
                .scoreAfterPlay(player != null ? player.getScore() : null)
                .questionText(questionText)
                .build();

        if (eventLogger != null) eventLogger.logEvent(event);
        gameEvents.add(event);
    }

    public Path generateSummaryReport() throws IOException {
        if (reportGenerator == null)
            throw new IllegalStateException("No report generator configured.");
        return reportGenerator.generate(this);
    }
}
