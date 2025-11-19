package com.jeopardy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.jeopardy.model.Category;
import com.jeopardy.model.GameState;
import com.jeopardy.model.Player;
import com.jeopardy.model.Question;

/**
 * Main game engine managing game state, players, and turns
 */
public class Game {
    private String gameId;
    private List<Player> players;
    private List<Category> categories;
    private GameState state;
    private int currentPlayerIndex;
    private int totalTurns;
    
    public Game() {
        this.gameId = "GAME_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.players = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.state = GameState.SETUP;
        this.currentPlayerIndex = 0;
        this.totalTurns = 0;
    }
    
    // Player management
    public void addPlayer(Player player) {
        if (state != GameState.SETUP) {
            throw new IllegalStateException("Cannot add players after game starts");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (players.size() >= 4) {
            throw new IllegalStateException("Maximum 4 players allowed");
        }
        players.add(player);
    }
    
    public void addPlayer(String name) {
        String playerId = "player" + (players.size() + 1);
        addPlayer(new Player(playerId, name));
    }
    
    // Category and question management
    public void setCategories(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("Categories cannot be null or empty");
        }
        this.categories = new ArrayList<>(categories);
    }
    
    public Question getQuestion(String categoryName, int value) {
        Category category = getCategory(categoryName);
        if (category != null) {
            return category.getQuestion(value);
        }
        return null;
    }
    
    public Category getCategory(String name) {
        return categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    
    // Game flow control
    public void startGame() {
        if (players.isEmpty()) {
            throw new IllegalStateException("Cannot start game without players");
        }
        if (categories.isEmpty()) {
            throw new IllegalStateException("Cannot start game without categories");
        }
        this.state = GameState.IN_PROGRESS;
        this.currentPlayerIndex = 0;
    }
    
    public void endGame() {
        this.state = GameState.FINISHED;
    }
    
    // Turn management
    public Player getCurrentPlayer() {
        if (players.isEmpty()) return null;
        return players.get(currentPlayerIndex);
    }
    
    public void nextTurn() {
        if (players.isEmpty() || state != GameState.IN_PROGRESS) return;
        
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        totalTurns++;
    }
    
    public void markQuestionAnswered(String categoryName, int value) {
        Question question = getQuestion(categoryName, value);
        if (question != null) {
            question.setAnswered(true);
        }
    }
    
    // Game state checks
    public boolean isGameFinished() {
        return state == GameState.FINISHED;
    }
    
    public boolean allQuestionsAnswered() {
        return categories.stream().allMatch(Category::allQuestionsAnswered);
    }
    
    public boolean hasAvailableQuestions() {
        return categories.stream().anyMatch(Category::hasAvailableQuestions);
    }
    
    public Player getWinner() {
        if (players.isEmpty() || state != GameState.FINISHED) return null;
        
        return players.stream()
                .max((p1, p2) -> Integer.compare(p1.getScore(), p2.getScore()))
                .orElse(null);
    }
    
    // Getters
    public String getGameId() { return gameId; }
    public List<Player> getPlayers() { return new ArrayList<>(players); }
    public List<Category> getCategories() { return new ArrayList<>(categories); }
    public GameState getState() { return state; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public int getTotalTurns() { return totalTurns; }
    public int getPlayerCount() { return players.size(); }
    
    @Override
    public String toString() {
        return String.format(
            "Game{id='%s', state=%s, players=%d, categories=%d, currentPlayer=%d}",
            gameId, state, players.size(), categories.size(), currentPlayerIndex
        );
    }
}