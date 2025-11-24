package com.jeopardy.service;

import com.jeopardy.model.Category;
import com.jeopardy.model.GameData;
import com.jeopardy.model.GameEvent;
import com.jeopardy.model.GameState;
import com.jeopardy.model.Player;
import com.jeopardy.model.Question;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameController {
    private GameState gameState;
    private GameData gameData;
    private final List<GameEvent> gameplayEvents = new ArrayList<>();

    private GameEventLogger eventLogger;
    private String caseId;
    private final SummaryReportGenerator reportGenerator = new TextSummaryReportGenerator();

    public GameController() {
        this.gameState = new GameState();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HHmmss");
        this.caseId = "GAME" + fmt.format(LocalDateTime.now());


        // CSV logger writes to game_event_log.csv
        this.eventLogger = new CsvGameEventLogger(caseId);
    }
    
    public List<GameEvent> getGameplayEvents() {
        return gameplayEvents;
    }

    public String getCaseId() {
        return caseId;
    }

    public GameState getGameState() {
        return gameState;
    }
    
    public List<Player> getPlayers() {
        return gameState.getPlayers();
    }
    
    public Player getCurrentPlayer() {
        return gameState.getCurrentPlayer();
    }
    
    public List<Category> getCategories() {
        return gameData != null ? gameData.getCategories() : List.of();
    }
    
    public Object getGame() {
        return this;
    }
    
    public Question getQuestion(String categoryName, int value) {
        if (gameData == null) return null;
        for (Category category : gameData.getCategories()) {
            if (category.getName().equals(categoryName)) {
                for (Question question : category.getAllQuestions()) {
                    if (question.getValue() == value) {
                        return question;
                    }
                }
            }
        }
        return null;
    }
    
    public void systemEvent(String activity, String category, Integer value, String extra) {
        if (eventLogger == null) {
            return;
        }

        GameEvent.Builder builder = new GameEvent.Builder(caseId, activity);

        switch (activity) {
        // ==========================================
        // Purely system-level events (no player)
        // ==========================================
        case "Load File":
        case "Start Game":
        case "Generate Report":
        case "Generate Event Log":
        case "Exit Game":
            builder.playerId("System");
            if (category != null && !category.isBlank()) {
                builder.category(category);
            }
            if (value != null) {
                builder.questionValue(value);
            }

            // If caller passes an explicit result (e.g. "Success"), use it.
            if (extra != null && !extra.isBlank()) {
                builder.result(extra);
            } else {
                // Otherwise default to "N/A" to match the sample log.
                builder.result("N/A");
            }
            break;

            // ==========================================
            // Select Player Count
            // Sample: ...,System,Select Player Count,...,,,2,N/A,
            // ==========================================
            case "Select Player Count":
                builder.playerId("System");
                if (extra != null && !extra.isBlank()) {
                    builder.answerGiven(extra); // the number of players, e.g. "2"
                }
                builder.result("N/A");
                break;

            // ==========================================
            // Enter Player Name
            // Sample: ...,Alice,Enter Player Name,...,,,Alice,N/A,
            // ==========================================
            case "Enter Player Name":
                String name = (extra != null) ? extra : "";
                builder.playerId(name);
                if (!name.isEmpty()) {
                    builder.answerGiven(name);
                }
                builder.result("N/A");
                break;

            // ==========================================
            // Player-driven navigation
            // Sample:
            //   Alice,Select Category,...,Category,,,,0
            //   Alice,Select Question,...,Category,100,,,0
            // Score_After_Play = current score *before* answering.
            // ==========================================
            case "Select Category":
            case "Select Question":
                Player current = getCurrentPlayer();
                if (current != null) {
                    builder.playerId(current.getName());
                    builder.scoreAfterPlay(current.getScore());
                } else {
                    builder.playerId("System");
                }

                if (category != null && !category.isBlank()) {
                    builder.category(category);
                }

                if ("Select Question".equals(activity) && value != null) {
                    builder.questionValue(value);
                }
                break;

            // ==========================================
            // Fallback (shouldn’t really be used, but safe)
            // ==========================================
            default:
                builder.playerId("System");
                if (category != null && !category.isBlank()) {
                    builder.category(category);
                }
                if (value != null) {
                    builder.questionValue(value);
                }
                if (extra != null && !extra.isBlank()) {
                    builder.result(extra);
                }
                break;
        }

        eventLogger.logEvent(builder.build());
    }

    public void initializeGame(List<String> names, GameData gameData) {
        this.gameData = gameData;
        gameState.setStatus(GameState.IN_PROGRESS);
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            Player player = new Player("P" + (i + 1), name);
            gameState.addPlayer(player);
        }
    }
    
    public void forceEndGame() {
        gameState.setStatus(GameState.FINISHED);
    }
    
    public boolean checkAndEndGame() {
        boolean allAnswered = true;
        if (gameData != null) {
            for (Category category : gameData.getCategories()) {
                for (Question question : category.getAllQuestions()) {
                    if (!question.isAnswered()) {
                        allAnswered = false;
                        break;
                    }
                }
            }
        }
        if (allAnswered) {
            gameState.setStatus(GameState.FINISHED);
        }
        return allAnswered;
    }
    
    public boolean isGameFinished() {
        return GameState.FINISHED.equals(gameState.getStatus());
    }
    
    public boolean answerQuestion(String categoryName, int value, String answer) {
        Question question = getQuestion(categoryName, value);
        if (question != null && !question.isAnswered()) {
            Player currentPlayer = getCurrentPlayer();
            if (currentPlayer == null) {
                return false;
            }

            boolean correct = question.getCorrectAnswer().equalsIgnoreCase(answer);

            if (correct) {
                currentPlayer.addPoints(question.getValue());
            } else {
                // Deduct points for wrong answer (but not below 0)
                currentPlayer.subtractPoints(question.getValue());
            }

            question.setAnswered(true);

            // Get the actual text of the chosen option, e.g. "int num;" or "Random value"
            String answerText = question.getOption(answer);
            if (answerText == null) {
                // Fallback to the letter if something goes weird
                answerText = answer;
            }

            // Log the answer event in the sample format style
            if (eventLogger != null) {
                GameEvent event = new GameEvent.Builder(caseId, "Answer Question")
                        .playerId(currentPlayer.getName())                 
                        .category(categoryName)
                        .questionValue(question.getValue())
                        .questionText(question.getQuestionText())
                        .answerGiven(answerText)                           
                        .result(correct ? "Correct" : "Incorrect")
                        .scoreAfterPlay(currentPlayer.getScore())          
                        .build();
                
                gameplayEvents.add(event);
                eventLogger.logEvent(event);
            }

            return correct;
        }
        return false;
    }

    public Path generateSummaryReport() throws IOException {
        return reportGenerator.generate(this);
    }

    
    public List<Player> getWinners() {
        return gameState.determineWinners();
    }
    
    public boolean isTie() {
        return gameState.isTie();
    }
    
    public String getGameResult() {
        return gameState.getGameResult();
    }
    
    public Player getWinner() {
        return gameState.getWinner();
    }

    public void nextPlayer() {
    gameState.nextPlayer();
}
}