package com.jeopardy.service;

import com.jeopardy.model.Category;
import com.jeopardy.model.GameState;
import com.jeopardy.model.Player;
import com.jeopardy.model.Question;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TextSummaryReportGenerator implements SummaryReportGenerator {

    @Override
    public Path generate(GameController controller) throws IOException {
        String fileName = "jeopardy_report_" + System.currentTimeMillis() + ".txt";
        Path filePath = Path.of(fileName);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toFile()))) {
            GameState gameState = controller.getGameState();
            List<Player> players = controller.getPlayers();
            List<Category> categories = controller.getCategories();
            
            writer.println("JEOPARDY PROGRAMMING GAME REPORT");
            writer.println("================================");
            writer.println();
            writer.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println();
            
            writer.println("PLAYERS:");
            writer.println("--------");
            for (Player player : players) {
                writer.println("- " + player.getName() + ": " + player.getScore() + " points");
            }
            writer.println();
            
            writer.println("GAMEPLAY SUMMARY:");
            writer.println("-----------------");
            
            List<Question> answeredQuestions = categories.stream()
                .flatMap(category -> category.getAllQuestions().stream())
                .filter(Question::isAnswered)
                .collect(Collectors.toList());
                
            int turn = 1;
            for (Question q : answeredQuestions) {
                writer.println("Turn " + turn + ": " + q.getCategory() + " - " + q.getValue() + " points");
                writer.println("  Question: " + q.getQuestionText());
                writer.println("  Correct Answer: " + q.getCorrectAnswer());
                turn++;
            }
            writer.println();
            
            writer.println("FINAL SCORES:");
            writer.println("-------------");
            for (Player player : players) {
                writer.println("- " + player.getName() + ": " + player.getScore() + " points");
            }
            writer.println();
            
            List<Player> winners = gameState.determineWinners();
            writer.println("FINAL RESULT:");
            writer.println("-------------");
            
            if (winners.isEmpty()) {
                writer.println("No winners!");
            } else if (winners.size() == 1) {
                writer.println("WINNER: " + winners.get(0).getName() + " with " + winners.get(0).getScore() + " points!");
            } else {
                writer.println("IT'S A TIE!");
                writer.println("Winners:");
                for (Player winner : winners) {
                    writer.println("- " + winner.getName() + ": " + winner.getScore() + " points");
                }
            }
            
            writer.println();
            writer.println("Thank you for playing Jeopardy!");
        }
        
        return filePath;
    }
}