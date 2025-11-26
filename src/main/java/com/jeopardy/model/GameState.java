package com.jeopardy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the current state of an entire Jeopardy game session.
 * <p>
 * This class tracks:
 * <ul>
 *     <li>The list of players and questions in the game</li>
 *     <li>The current player and their index</li>
 *     <li>The overall game status (SETUP, IN_PROGRESS, FINISHED)</li>
 *     <li>Logic for determining winners and detecting ties</li>
 * </ul>
 */
public class GameState {
    /** Game is being configured (players/questions not yet in active play). */
    public static final String SETUP = "SETUP";

    /** Game is actively in progress. */
    public static final String IN_PROGRESS = "IN_PROGRESS";

    /** Game has ended and final scores are available. */
    public static final String FINISHED = "FINISHED";
    
    /** All players participating in the current game. */
    private List<Player> players;

    /** All questions included in this game session. */
    private List<Question> questions;

    /** The player whose turn it currently is. */
    private Player currentPlayer;

    /** Current lifecycle status of the game (SETUP/IN_PROGRESS/FINISHED). */
    private String status;

    /** Index of the current player within the players list. */
    private int currentPlayerIndex;
    
    /**
     * Creates a new {@code GameState} with empty player and question lists,
     * status set to {@link #SETUP}, and the current player index at 0.
     */
    public GameState() {
        this.players = new ArrayList<>();
        this.questions = new ArrayList<>();
        this.status = SETUP;
        this.currentPlayerIndex = 0;
    }
    
    /**
     * Returns the list of players in the game.
     *
     * @return the list of players
     */
    public List<Player> getPlayers() {
        return players;
    }
    
    /**
     * Replaces the current list of players with the given list.
     *
     * @param players the new list of players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    
    /**
     * Returns the list of questions in the game.
     *
     * @return the list of questions
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * Replaces the current list of questions with the given list.
     *
     * @param questions the new list of questions
     */
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    
     /**
     * Returns the player whose turn it is.
     *
     * @return the current player, or {@code null} if no players have been added
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Explicitly sets the current player.
     *
     * @param currentPlayer the player whose turn it should be
     */
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    
    /**
     * Returns the current game status.
     *
     * @return one of {@link #SETUP}, {@link #IN_PROGRESS}, {@link #FINISHED}
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Updates the game status.
     *
     * @param status the new status value
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Returns the index of the current player within the players list.
     *
     * @return the current player index
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    
    /**
     * Sets the current player index.
     *
     * @param currentPlayerIndex the index of the current player
     */
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }
    
    /**
     * Adds a player to the game.
     * <p>
     * If this is the first player added, they also become the current player.
     *
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        players.add(player);
        if (currentPlayer == null) {
            currentPlayer = player;
        }
    }
    
    /**
     * Adds a question to the game.
     *
     * @param question the question to add
     */
    public void addQuestion(Question question) {
        questions.add(question);
    }
    
    /** 
     * Moves to the next player in the list, wrapping back to the beginning
     * once the end is reached.
     * <p>
     * If there are no players, this method does nothing.
     */
    public void nextPlayer() {
    if (players.isEmpty()) return;
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    currentPlayer = players.get(currentPlayerIndex);
}
    
    /**
     * Checks whether the game has finished.
     *
     * @return {@code true} if the status is {@link #FINISHED}, otherwise {@code false}
     */
    public boolean isGameFinished() {
        return FINISHED.equals(status);
    }
    
    /**
     * Determines the winning player(s) based on the highest score.
     * <p>
     * If there are no players, an empty list is returned. If multiple players
     * share the highest score, they are all included in the result.
     *
     * @return a list of players who have the highest score
     */
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
    
    /**
     * Checks whether the game ended in a tie (more than one winner).
     *
     * @return {@code true} if multiple players share the highest score,
     *         {@code false} otherwise
     */
    public boolean isTie() {
        List<Player> winners = determineWinners();
        return winners.size() > 1;
    }
    
    /**
     * Builds a human-readable result message describing the outcome of the game.
     * <ul>
     *     <li>Returns {@code "No winners!"} if there were no players.</li>
     *     <li>Returns a single-winner message if exactly one winner exists.</li>
     *     <li>Returns a tie message listing all winners and their scores.</li>
     * </ul>
     *
     * @return a formatted string describing the game result
     */
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
    
    /**
     * Returns a single winner if there is one, or the first winner in the
     * list when there are multiple winners.
     *
     * @return the winning player, or {@code null} if there are no winners
     */
    public Player getWinner() {
        List<Player> winners = determineWinners();
        return winners.isEmpty() ? null : winners.get(0);
    }
}