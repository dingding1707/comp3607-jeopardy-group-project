package com.jeopardy.model;

import java.time.Instant;

/** Represents a game event for logging and process mining. */
public class GameEvent {

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

    /** Creates a GameEvent from a Builder. */
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

    // Getters
    public String getCaseId() { return caseId; }
    public String getPlayerId() { return playerId; }
    public String getActivity() { return activity; }
    public Instant getTimestamp() { return timestamp; }
    public String getCategory() { return category; }
    public Integer getQuestionValue() { return questionValue; }
    public String getAnswerGiven() { return answerGiven; }
    public String getResult() { return result; }
    public Integer getScoreAfterPlay() { return scoreAfterPlay; }
    public String getQuestionText() { return questionText; }

    // ============================
    //          BUILDER
    // ============================
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

        /** Creates a Builder with case ID and activity. */
        public Builder(String caseId, String activity) {
            this.caseId = caseId;
            this.activity = activity;
            this.timestamp = Instant.now();
        }

        /** Sets the player ID. */
        public Builder playerId(String playerId) {
            this.playerId = playerId;
            return this;
        }

        /** Sets the category. */
        public Builder category(String category) {
            this.category = category;
            return this;
        }

        /** Sets the question value. */
        public Builder questionValue(Integer questionValue) {
            this.questionValue = questionValue;
            return this;
        }

        /** Sets the answer given. */
        public Builder answerGiven(String answerGiven) {
            this.answerGiven = answerGiven;
            return this;
        }

        /** Sets the result. */
        public Builder result(String result) {
            this.result = result;
            return this;
        }

        /** Sets the score after play. */
        public Builder scoreAfterPlay(Integer scoreAfterPlay) {
            this.scoreAfterPlay = scoreAfterPlay;
            return this;
        }

        /** Sets the timestamp. */
        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /** Sets the question text. */
        public Builder questionText(String questionText) {
            this.questionText = questionText;
            return this;
        }

        /** Builds and returns the GameEvent. */
        public GameEvent build() {
            return new GameEvent(this);
        }
    }

    @Override
    public String toString() {
        return String.format(
            "GameEvent{caseId='%s', playerId='%s', activity='%s', timestamp=%s}",
            caseId, playerId, activity, timestamp
        );
    }
}
