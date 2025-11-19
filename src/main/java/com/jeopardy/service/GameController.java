package com.jeopardy.service;

//import com.jeopardy.model.Game;
import java.util.List;

import com.jeopardy.model.Category;
import com.jeopardy.model.GameData;
import com.jeopardy.model.GameState;
import com.jeopardy.model.Player;
import com.jeopardy.model.Question;

/**
 * Main controller that orchestrates the entire game flow
 */
public class GameController {
    private Game game;
    private ScoreManager scoreManager;
    private GameData gameData;
    
    public GameController() {
        this.game = new Game();
        this.scoreManager = new ScoreManager();
    }
    
    /**
     * Initialize the game with players and data
     */
    public void initializeGame(List<String> playerNames, GameData gameData) {
        if (playerNames == null || playerNames.isEmpty()) {
            throw new IllegalArgumentException("Player names cannot be null or empty");
        }
        if (gameData == null || gameData.isEmpty()) {
            throw new IllegalArgumentException("Game data cannot be null or empty");
        }
        
        // Add players
        for (String name : playerNames) {
            game.addPlayer(name.trim());
        }
        
        // Set game data
        this.gameData = gameData;
        game.setCategories(gameData.getCategories());
        
        // Start the game
        game.startGame();
    }
    
    /**
     * Handle a player selecting and answering a question
     */
    public boolean answerQuestion(String categoryName, int questionValue, String playerAnswer) {
        if (game.getState() != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress");
        }
        
        Player currentPlayer = game.getCurrentPlayer();
        Question question = game.getQuestion(categoryName, questionValue);
        
        if (question == null) {
            throw new IllegalArgumentException("Question not found: " + categoryName + " - " + questionValue);
        }
        if (question.isAnswered()) {
            throw new IllegalStateException("Question has already been answered");
        }
        if (playerAnswer == null || playerAnswer.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer cannot be null or empty");
        }
        
        // Check if answer is correct
        boolean isCorrect = question.isCorrect(playerAnswer);
        
        // Update score
        scoreManager.updateScore(currentPlayer, questionValue, isCorrect);
        
        // Mark question as answered
        question.setAnswered(true);
        
        // Move to next turn
        game.nextTurn();
        
        return isCorrect;
    }
    
    /**
     * Check if game should end and end it if conditions are met
     */
    public boolean checkAndEndGame() {
        if (!game.hasAvailableQuestions() || game.allQuestionsAnswered()) {
            game.endGame();
            return true;
        }
        return false;
    }
    
    /**
     * Get available categories (those with unanswered questions)
     */
    public List<Category> getAvailableCategories() {
        return game.getCategories().stream()
                .filter(Category::hasAvailableQuestions)
                .toList();
    }
    
    /**
     * Get available questions for a category
     */
    public List<Question> getAvailableQuestions(String categoryName) {
        Category category = game.getCategory(categoryName);
        if (category != null) {
            return category.getAllQuestions().stream()
                    .filter(q -> !q.isAnswered())
                    .toList();
        }
        return List.of();
    }
    
    /**
     * Change scoring strategy during game
     */
    public void setScoringStrategy(ScoringStrategy strategy) {
        scoreManager.setScoringStrategy(strategy);
    }
    
    // Getters for UI and other components
    public Game getGame() { return game; }
    public ScoreManager getScoreManager() { return scoreManager; }
    public GameData getGameData() { return gameData; }
    public Player getCurrentPlayer() { return game.getCurrentPlayer(); }
    public List<Player> getPlayers() { return game.getPlayers(); }
    public List<Category> getCategories() { return game.getCategories(); }
    public GameState getGameState() { return game.getState(); }
    public boolean isGameFinished() { return game.isGameFinished(); }
    public Player getWinner() { return game.getWinner(); }
    
    /**
     * Get game summary for reporting
     */
    public String getGameSummary() {
        return String.format(
            "Game ID: %s\nPlayers: %d\nTotal Turns: %d\nGame State: %s\nScoring: %s",
            game.getGameId(),
            game.getPlayerCount(),
            game.getTotalTurns(),
            game.getState(),
            scoreManager.getScoringStrategyName()
        );
    }
}