package com.jeopardy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Represents the current state of the game. */
public class GameState {
    public static final String SETUP = "SETUP";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String FINISHED = "FINISHED";
    
    private List<Player> players;
    private List<Question> questions;
    private Player currentPlayer;
    private String status;
    private int currentPlayerIndex;
    
    /** Creates a new GameState with initial values. */
    public GameState() {
        this.players = new ArrayList<>();
        this.questions = new ArrayList<>();
        this.status = SETUP;
        this.currentPlayerIndex = 0;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    
    public List<Question> getQuestions() {
        return questions;
    }
   
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }
    
    public void addPlayer(Player player) {
        players.add(player);
        if (currentPlayer == null) {
            currentPlayer = player;
        }
    }
    
    public void addQuestion(Question question) {
        questions.add(question);
    }
    
    /** Moves to the next player. */
    public void nextPlayer() {
    if (players.isEmpty()) return;
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    currentPlayer = players.get(currentPlayerIndex);
}
    
    /** Checks if the game is finished. */
    public boolean isGameFinished() {
        return FINISHED.equals(status);
    }
    
    /** Determines the winner(s) of the game. */
    public List<Player> determineWinners() {
        List<Player> winners = new ArrayList<>();
        if (players == null || players.isEmpty()) {
            return winners;
        }
        
        int highestScore = players.stream()
            .mapToInt(Player::getScore)
            .max()
            .orElse(0);
        
        winners = players.stream()
            .filter(player -> player.getScore() == highestScore)
            .collect(Collectors.toList());
            
        return winners;
    }
    
    /** Checks if the game ended in a tie. */
    public boolean isTie() {
        List<Player> winners = determineWinners();
        return winners.size() > 1;
    }
    
    /** Gets the game result message. */
    public String getGameResult() {
        List<Player> winners = determineWinners();
        
        if (winners.isEmpty()) {
            return "No winners!";
        } else if (winners.size() == 1) {
            return "Winner: " + winners.get(0).getName() + " with " + winners.get(0).getScore() + " points!";
        } else {
            StringBuilder tieMessage = new StringBuilder("It's a tie! Winners: ");
            for (int i = 0; i < winners.size(); i++) {
                tieMessage.append(winners.get(i).getName())
                         .append(" (").append(winners.get(i).getScore()).append(" points)");
                if (i < winners.size() - 1) {
                    tieMessage.append(", ");
                }
            }
            return tieMessage.toString();
        }
    }
    
    /** Gets the single winner. */
    public Player getWinner() {
        List<Player> winners = determineWinners();
        return winners.isEmpty() ? null : winners.get(0);
    }
}