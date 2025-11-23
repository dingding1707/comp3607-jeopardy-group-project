package com.jeopardy.service;

import com.jeopardy.model.GameData;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CsvGameDataLoaderTest {

    @Test
    void load_validCsv_shouldPassValidation() throws Exception {
        String csv = String.join("\n",
                "Category,Value,Question,OptionA,OptionB,OptionC,OptionD,CorrectAnswer",
                "Science,100,What is H2O?,Hydrogen,Oxygen,Water,Helium,C"
        );

        Path temp = Files.createTempFile("jeopardy-valid", ".csv");
        Files.writeString(temp, csv);

        CsvGameDataLoader loader = new CsvGameDataLoader();
        GameData data = loader.load(temp);

        assertNotNull(data);
        assertEquals(1, data.getTotalQuestions());
        assertEquals(1, data.getTotalCategories());
        Files.deleteIfExists(temp);
    }

    @Test
    void load_missingOption_shouldFailValidation() throws Exception {
        String csv = String.join("\n",
                "Category,Value,Question,OptionA,OptionB,OptionC,OptionD,CorrectAnswer",
                // OptionD empty → should trigger DataValidator
                "Science,100,What is H2O?,Hydrogen,Oxygen,Water,,C"
        );

        Path temp = Files.createTempFile("jeopardy-invalid", ".csv");
        Files.writeString(temp, csv);

        CsvGameDataLoader loader = new CsvGameDataLoader();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> loader.load(temp)
        );
        assertTrue(ex.getMessage().contains("Option D") ||
                   ex.getMessage().contains("empty"),
                   "Message should mention empty option");
        Files.deleteIfExists(temp);
    }
}
