package com.jeopardy.model;

/**
 * Represents a player in the Jeopardy game.
 * <p>
 * Each player has a unique ID, a display name, and a score that is always
 * maintained as a non-negative integer.
 */
public class Player{

    /** Unique identifier for this player (used in logs, events, etc.). */
    private String playerId;

    /** Display name of the player (shown in UI and reports). */
    private String name;

    /** Current score for this player (never negative). */
    private int score;

    /**
     * Creates a player with the given ID and name.
     * The initial score is set to zero.
     *
     * @param playerId unique identifier for the player
     * @param name     display name of the player
     */
    public Player(String playerId, String name){
        this.playerId = playerId;
        this.name = name;
        this.score = 0;  
    }

    /**
     * Returns the display name of this player.
     *
     * @return the player's name
     */
    public String getName(){
        return this.name;
    }
   
    /**
     * Returns the unique identifier of this player.
     *
     * @return the player ID
     */
    public String getPlayerId(){
        return this.playerId;
    }

    /**
     * Returns the current score of this player.
     *
     * @return the player's score
     */
    public int getScore(){
        return this.score;
    }

    /**
     * Sets the player's score, as long as the provided value is not negative.
     * <p>
     * If a negative value is passed, the score is left unchanged.
     *
     * @param score the new score value (must be non-negative)
     */
    public void setScore(int score){
        if(score >= 0)
            this.score = score;
    }

    /**
     * Adds the specified number of points to this player's score.
     * <p>
     * Only positive point values are accepted; non-positive values are ignored.
     *
     * @param points the number of points to add (must be greater than zero)
     */
    public void addPoints(int points){
        if(points > 0)
            this.score += points;
    }

    /**
     * Subtracts the specified number of points from this player's score
     * while ensuring that the score never becomes negative.
     * <p>
     * Only positive point values are considered; non-positive values are ignored.
     *
     * @param points the number of points to subtract (must be greater than zero)
     */
    public void subtractPoints(int points) {
        if (points > 0) {
            this.score = Math.max(0, this.score - points); // Prevent negative scores
        }
    }
    
    /**
     * Resets the player's score back to zero.
     */
    public void resetScore() {
        this.score = 0;
    }
    
    /**
     * Returns a string representation of this player, including ID, name,
     * and current score.
     *
     * @return a string describing this player
     */
    @Override
    public String toString() {
        return String.format("Player{id='%s', name='%s', score=%d}", playerId, name, score);
    }
    
    /**
     * Compares this player to another object for equality.
     * <p>
     * Two players are considered equal if they are of the same class and
     * have the same {@code playerId}.
     *
     * @param obj the object to compare with
     * @return {@code true} if the players have the same ID, otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return playerId.equals(player.playerId);
    }
    
    /**
     * Returns a hash code based on this player's ID.
     *
     * @return the hash code for this player
     */
    @Override
    public int hashCode() {
        return playerId.hashCode();
    }
}
