package com.jeopardy.service;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Strategy interface for generating a summary report of a completed game.
 */
public interface SummaryReportGenerator {

    /**
     * Generate a report for the current game managed by the controller.
     *
     * @param controller controller with a finished game
     * @return path to the generated report file
     */
    Path generate(GameController controller) throws IOException;
}
