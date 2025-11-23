package com.jeopardy.service;

import com.jeopardy.model.GameData;
import com.jeopardy.model.Question;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads Jeopardy data from a JSON file using the structure:
 *
 * [
 *   {
 *     "Category": "Science",
 *     "Value": 100,
 *     "Question": "What is H2O?",
 *     "Options": {
 *        "A": "Hydrogen",
 *        "B": "Oxygen",
 *        "C": "Water",
 *        "D": "Helium"
 *     },
 *     "CorrectAnswer": "C"
 *   },
 *   ...
 * ]
 */
public class JsonGameDataLoader implements GameDataLoader {

    @Override
    public GameData load(Path filePath) throws IOException {

        if (filePath == null) {
            throw new IllegalArgumentException("JSON file path cannot be null.");
        }

        if (!Files.exists(filePath)) {
            throw new IOException("JSON file does not exist: " + filePath);
        }

        String content = Files.readString(filePath).trim();

        if (content.isEmpty()) {
            throw new IOException("JSON file is empty: " + filePath);
        }

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(content);
        } catch (Exception e) {
            throw new IOException("Invalid JSON format: " + e.getMessage());
        }

        GameData gameData = new GameData();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            // ---- VALIDATE REQUIRED FIELDS ----
            String category = getRequiredString(obj, "Category", i);
            int value = getRequiredInt(obj, "Value", i);
            String questionText = getRequiredString(obj, "Question", i);
            String correctAnswer = getRequiredString(obj, "CorrectAnswer", i).toUpperCase();

            if (!"ABCD".contains(correctAnswer)) {
                throw new IOException("Invalid correct answer '" + correctAnswer +
                        "' at question index " + i + ". Must be A, B, C, or D.");
            }

            // ---- OPTIONS VALIDATION ----
            if (!obj.has("Options") || obj.isNull("Options")) {
                throw new IOException("Missing 'Options' object at question index " + i);
            }

            JSONObject optionsObj = obj.getJSONObject("Options");

            Map<String, String> options = new HashMap<>();

            for (String key : new String[]{"A", "B", "C", "D"}) {
                if (!optionsObj.has(key)) {
                    throw new IOException("Missing option '" + key + "' in question at index " + i);
                }
                String opt = optionsObj.getString(key).trim();
                if (opt.isEmpty()) {
                    throw new IOException("Option '" + key + "' cannot be empty at question index " + i);
                }
                options.put(key, opt);
            }

            // ---- CREATE QUESTION ----
            Question q = new Question(category, value, questionText, options, correctAnswer);

            gameData.addQuestion(q);
        }

        // ---- VALIDATE THE FINAL DATA TREE ----
        DataValidator.validateCategories(gameData.getCategories());

        return gameData;
    }

    // -------------------------
    //   Helper validation methods
    // -------------------------

    private String getRequiredString(JSONObject obj, String field, int index) throws IOException {
        if (!obj.has(field) || obj.isNull(field)) {
            throw new IOException("Missing field '" + field + "' at question index " + index);
        }
        String val = obj.getString(field).trim();
        if (val.isEmpty()) {
            throw new IOException("Field '" + field + "' cannot be empty at question index " + index);
        }
        return val;
    }

    private int getRequiredInt(JSONObject obj, String field, int index) throws IOException {
        if (!obj.has(field) || obj.isNull(field)) {
            throw new IOException("Missing numeric field '" + field + "' at question index " + index);
        }
        try {
            return obj.getInt(field);
        } catch (Exception e) {
            throw new IOException("Invalid numeric value for '" + field + "' at question index "
                    + index + ": " + e.getMessage());
        }
    }
}
