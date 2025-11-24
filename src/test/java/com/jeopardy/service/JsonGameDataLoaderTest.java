// package com.jeopardy.service;

// import com.jeopardy.model.GameData;
// import org.junit.jupiter.api.Test;

// import java.nio.file.Files;
// import java.nio.file.Path;

// import static org.junit.jupiter.api.Assertions.*;

// class JsonGameDataLoaderTest {

//     @Test
//     void load_validJson_shouldPassValidation() throws Exception {
//         String json =
//                 "[\n" +
//                 "  {\n" +
//                 "    \"Category\": \"Science\",\n" +
//                 "    \"Value\": 100,\n" +
//                 "    \"Question\": \"What is H2O?\",\n" +
//                 "    \"Options\": {\n" +
//                 "      \"A\": \"Hydrogen\",\n" +
//                 "      \"B\": \"Oxygen\",\n" +
//                 "      \"C\": \"Water\",\n" +
//                 "      \"D\": \"Helium\"\n" +
//                 "    },\n" +
//                 "    \"CorrectAnswer\": \"C\"\n" +
//                 "  }\n" +
//                 "]\n";

//         Path temp = Files.createTempFile("jeopardy-valid", ".json");
//         Files.writeString(temp, json);

//         JsonGameDataLoader loader = new JsonGameDataLoader();
//         GameData data = loader.load(temp);

//         assertNotNull(data);
//         assertEquals(1, data.getTotalQuestions());
//         Files.deleteIfExists(temp);
//     }

//     @Test
//     void load_invalidCorrectAnswer_shouldFailValidation() throws Exception {
//         String json =
//                 "[\n" +
//                 "  {\n" +
//                 "    \"Category\": \"Science\",\n" +
//                 "    \"Value\": 100,\n" +
//                 "    \"Question\": \"What is H2O?\",\n" +
//                 "    \"Options\": {\n" +
//                 "      \"A\": \"Hydrogen\",\n" +
//                 "      \"B\": \"Oxygen\",\n" +
//                 "      \"C\": \"Water\",\n" +
//                 "      \"D\": \"Helium\"\n" +
//                 "    },\n" +
//                 "    \"CorrectAnswer\": \"Z\"\n" +
//                 "  }\n" +
//                 "]\n";

//         Path temp = Files.createTempFile("jeopardy-invalid", ".json");
//         Files.writeString(temp, json);

//         JsonGameDataLoader loader = new JsonGameDataLoader();

//         Exception ex = assertThrows(
//                 Exception.class,
//                 () -> loader.load(temp)
//         );
//         assertTrue(
//                 ex.getMessage().toLowerCase().contains("correct") ||
//                 ex.getMessage().toLowerCase().contains("invalid"),
//                 "Message should mention invalid correct answer"
//         );
//         Files.deleteIfExists(temp);
//     }
// }
