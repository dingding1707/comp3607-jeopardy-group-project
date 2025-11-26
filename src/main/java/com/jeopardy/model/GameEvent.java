package com.jeopardy.model;

import java.time.Instant;

/**
 * Represents a single event occurring during a Jeopardy game.
 * <p>
 * Game events are used for logging and process mining, and typically
 * correspond to actions such as answering a question or recording a
 * system-level activity.
 */
public class GameEvent {

    /** Unique identifier for the game session (case). */
    private String caseId;

    /** Identifier of the player associated with this event, or "System". */
    private String playerId;

    /** Name of the activity being recorded (e.g. "Answer Question"). */
    private String activity;

    /** Timestamp at which the event occurred. */
    private Instant timestamp;

    /** Category name associated with the event, if applicable. */
    private String category;

    /** Question value (points) associated with the event, if applicable. */
    private Integer questionValue;

    /** The answer selected or given by the player, if applicable. */
    private String answerGiven;

    /** Outcome or result of the event (e.g. "Correct", "Incorrect", "Success"). */
    private String result;

    /** Player's score immediately after this event is applied. */
    private Integer scoreAfterPlay;

    /** Text of the question associated with this event, if applicable. */
    private String questionText;

    /**
     * Creates a {@code GameEvent} instance from the supplied {@link Builder}.
     * <p>
     * If the builder does not provide a timestamp, the current instant is used.
     *
     * @param builder the builder containing all event fields
     */
    private GameEvent(Builder builder) {
        this.caseId = builder.caseId;
        this.playerId = builder.playerId;
        this.activity = builder.activity;
        this.timestamp = builder.timestamp != null ? builder.timestamp : Instant.now();
        this.category = builder.category;
        this.questionValue = builder.questionValue;
        this.answerGiven = builder.answerGiven;
        this.result = builder.result;
        this.scoreAfterPlay = builder.scoreAfterPlay;
        this.questionText = builder.questionText;  // NEW
    }

    /**
     * Returns the case ID associated with this event.
     *
     * @return the case identifier
     */
    public String getCaseId() { return caseId; }

    /**
     * Returns the player ID associated with this event.
     *
     * @return the player identifier, or "System" for system events
     */
    public String getPlayerId() { return playerId; }

    /**
     * Returns the name of the activity represented by this event.
     *
     * @return the activity name
     */
    public String getActivity() { return activity; }

    /**
     * Returns the timestamp at which this event occurred.
     *
     * @return the event timestamp
     */
    public Instant getTimestamp() { return timestamp; }

    /**
     * Returns the category associated with this event, if any.
     *
     * @return the category name, or {@code null} if not applicable
     */
    public String getCategory() { return category; }

    /**
     * Returns the question value associated with this event, if any.
     *
     * @return the question value in points, or {@code null} if not applicable
     */
    public Integer getQuestionValue() { return questionValue; }

     /**
     * Returns the answer that was given or recorded for this event.
     *
     * @return the answer text, or {@code null} if not applicable
     */
    public String getAnswerGiven() { return answerGiven; }

    /**
     * Returns the result or outcome of this event.
     *
     * @return the result string (e.g. "Correct", "Incorrect", "Success")
     */
    public String getResult() { return result; }

    /**
     * Returns the player's score immediately after this event.
     *
     * @return the score after the event, or {@code null} if not applicable
     */
    public Integer getScoreAfterPlay() { return scoreAfterPlay; }

    /**
     * Returns the question text associated with this event, if any.
     *
     * @return the question text, or {@code null} if not applicable
     */
    public String getQuestionText() { return questionText; }

    //          BUILDER

    /**
     * Builder for constructing {@link GameEvent} instances in a fluent way.
     * <p>
     * Only {@code caseId} and {@code activity} are required; all other fields
     * are optional and can be set via the corresponding methods.
     */
    public static class Builder {

        private String caseId;
        private String playerId;
        private String activity;
        private Instant timestamp;
        private String category;
        private Integer questionValue;
        private String answerGiven;
        private String result;
        private Integer scoreAfterPlay;
        private String questionText;

        /**
         * Creates a new builder with the required case ID and activity.
         * A default timestamp of {@link Instant#now()} is assigned.
         *
         * @param caseId   the unique identifier of the game session
         * @param activity the name of the activity represented by this event
         */
        public Builder(String caseId, String activity) {
            this.caseId = caseId;
            this.activity = activity;
            this.timestamp = Instant.now();
        }

        /**
         * Sets the ID of the player associated with this event.
         *
         * @param playerId the player identifier (or "System" for system events)
         * @return this builder instance for method chaining
         */
        public Builder playerId(String playerId) {
            this.playerId = playerId;
            return this;
        }

        /**
         * Sets the category associated with this event.
         *
         * @param category the category name
         * @return this builder instance for method chaining
         */
        public Builder category(String category) {
            this.category = category;
            return this;
        }

        /**
         * Sets the point value of the question associated with this event.
         *
         * @param questionValue the question value in points
         * @return this builder instance for method chaining
         */
        public Builder questionValue(Integer questionValue) {
            this.questionValue = questionValue;
            return this;
        }

        /**
         * Sets the answer given as part of this event.
         *
         * @param answerGiven the answer text or label
         * @return this builder instance for method chaining
         */
        public Builder answerGiven(String answerGiven) {
            this.answerGiven = answerGiven;
            return this;
        }

        /**
         * Sets the outcome or result of this event.
         *
         * @param result the result string (e.g. "Correct", "Incorrect", "Success")
         * @return this builder instance for method chaining
         */
        public Builder result(String result) {
            this.result = result;
            return this;
        }

        /**
         * Sets the player's score after this event has been applied.
         *
         * @param scoreAfterPlay the score after the event
         * @return this builder instance for method chaining
         */
        public Builder scoreAfterPlay(Integer scoreAfterPlay) {
            this.scoreAfterPlay = scoreAfterPlay;
            return this;
        }

        /**
         * Overrides the timestamp for this event.
         *
         * @param timestamp the timestamp to use instead of {@link Instant#now()}
         * @return this builder instance for method chaining
         */
        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Sets the question text associated with this event.
         *
         * @param questionText the full question text
         * @return this builder instance for method chaining
         */
        public Builder questionText(String questionText) {
            this.questionText = questionText;
            return this;
        }

        /**
         * Builds and returns a new {@link GameEvent} instance using the
         * values currently set on this builder.
         *
         * @return a fully constructed {@code GameEvent}
         */
        public GameEvent build() {
            return new GameEvent(this);
        }
    }

    /**
     * Returns a concise string representation of this event, including
     * the case ID, player ID, activity name and timestamp.
     *
     * @return a string summary of this {@code GameEvent}
     */
    @Override
    public String toString() {
        return String.format(
            "GameEvent{caseId='%s', playerId='%s', activity='%s', timestamp=%s}",
            caseId, playerId, activity, timestamp
        );
    }
}
