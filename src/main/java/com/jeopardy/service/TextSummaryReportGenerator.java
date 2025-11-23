package com.jeopardy.service;

import com.jeopardy.model.Category;
import com.jeopardy.model.Player;
import com.jeopardy.model.Question;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Plain-text implementation of the summary report generator.
 */

public class TextSummaryReportGenerator implements SummaryReportGenerator {

    @Override
    public Path generate(GameController controller) throws IOException {
        if (controller == null || !controller.isGameFinished()) {
            throw new IllegalStateException("Game must be finished before generating a report.");
        }

        var game = controller.getGame();

        Path reportsDir = Paths.get("reports");
        Files.createDirectories(reportsDir);

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path reportFile = reportsDir.resolve(
                "game_summary_" + game.getGameId() + "_" + timestamp + ".txt"
        );

        try (BufferedWriter writer = Files.newBufferedWriter(reportFile)) {
            writer.write("JEOPARDY GAME SUMMARY");
            writer.newLine();
            writer.write("=====================");
            writer.newLine();
            writer.newLine();

            // High level summary string already provided by controller
            writer.write(controller.getGameSummary());
            writer.newLine();
            writer.newLine();

            // Final scores
            writer.write("Final Scores:");
            writer.newLine();
            for (Player p : controller.getPlayers()) {
                writer.write(String.format(" - %s (%s): %d points",
                        p.getName(), p.getPlayerId(), p.getScore()));
                writer.newLine();
            }

            writer.newLine();
            writer.write("Category Overview:");
            writer.newLine();
            for (Category c : controller.getCategories()) {
                int total = c.getAllQuestions().size();
                long answered = c.getAllQuestions().stream()
                        .filter(Question::isAnswered)
                        .count();

                writer.write(String.format(" - %s: %d / %d questions answered",
                        c.getName(), answered, total));
                writer.newLine();
            }
        }

        return reportFile;
    }
}
