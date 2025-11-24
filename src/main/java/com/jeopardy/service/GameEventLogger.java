package com.jeopardy.service;

import com.jeopardy.model.GameEvent;

/** Interface for logging game events. */

public interface GameEventLogger {
    void logEvent(GameEvent event);
    void close();
}
