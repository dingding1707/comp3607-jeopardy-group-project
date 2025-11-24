package com.jeopardy.service;

import java.io.IOException;
import java.nio.file.Path;

/** Interface for generating game reports. */
public interface SummaryReportGenerator {

    /** Generates a report for the game. */
    Path generate(GameController controller) throws IOException;
}
