package com.jeopardy.service;

import com.jeopardy.model.GameData;
import java.io.IOException;
import java.nio.file.Path;

/** Interface for loading game data from files. */
public interface GameDataLoader {
    /** Loads game data from the specified file. */
    GameData load(Path path) throws IOException;
}
