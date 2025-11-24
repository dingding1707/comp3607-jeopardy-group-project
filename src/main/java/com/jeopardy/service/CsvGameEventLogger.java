package com.jeopardy.service;

import com.jeopardy.model.GameEvent;
import com.jeopardy.model.GameState;
import com.jeopardy.model.Player;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class CsvGameEventLogger implements GameEventLogger {
    private static final String CSV_HEADER = "Case_ID,Player_ID,Activity,Timestamp,Category,Question_Value,Answer_Given,Result,Score_After";
    private String caseId;
    private String logFilePath;
    
    public CsvGameEventLogger(String caseId) {
        this.caseId = caseId;
        this.logFilePath = "game_event_log.csv";
        initializeLogFile();
    }
    
    private void initializeLogFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFilePath, false))) {
            writer.println(CSV_HEADER);
        } catch (IOException e) {
            System.err.println("Error initializing log file: " + e.getMessage());
        }
    }
    
    @Override
    public void logEvent(GameEvent event) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFilePath, true))) {
            String timestamp = event.getTimestamp().toString();
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%d%n",
                event.getCaseId(),
                event.getPlayerId() != null ? event.getPlayerId() : "System",
                event.getActivity(),
                timestamp,
                event.getCategory() != null ? event.getCategory() : "N/A",
                event.getQuestionValue() != null ? event.getQuestionValue() : "N/A",
                event.getAnswerGiven() != null ? event.getAnswerGiven() : "N/A",
                event.getResult() != null ? event.getResult() : "N/A",
                event.getScoreAfterPlay() != null ? event.getScoreAfterPlay() : 0
            );
        } catch (IOException e) {
            System.err.println("Error logging event: " + e.getMessage());
        }
    }
    
    public void logGameEnd(GameState gameState) {
        List<Player> winners = gameState.determineWinners();
        String result;
        
        if (winners.isEmpty()) {
            result = "No winners";
        } else if (winners.size() == 1) {
            result = "Winner: " + winners.get(0).getName();
        } else {
            result = "Tie: " + winners.stream()
                    .map(Player::getName)
                    .collect(Collectors.joining(", "));
        }
        
        GameEvent endEvent = new GameEvent.Builder(caseId, "Game End")
            .result(result)
            .build();
        logEvent(endEvent);
        
        for (Player winner : winners) {
            GameEvent winnerEvent = new GameEvent.Builder(caseId, "Final Score")
                .playerId(winner.getPlayerId())
                .result("Winner")
                .scoreAfterPlay(winner.getScore())
                .build();
            logEvent(winnerEvent);
        }
    }
    
    @Override
    public void close() {
        GameEvent closeEvent = new GameEvent.Builder(caseId, "Close Logger")
            .result("Success")
            .build();
        logEvent(closeEvent);
    }
}