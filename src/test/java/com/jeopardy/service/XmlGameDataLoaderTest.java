// package com.jeopardy.service;

// import com.jeopardy.model.GameData;
// import org.junit.jupiter.api.Test;

// import java.nio.file.Files;
// import java.nio.file.Path;

// import static org.junit.jupiter.api.Assertions.*;

// class XmlGameDataLoaderTest {

//     @Test
//     void load_validXml_shouldPassValidation() throws Exception {
//         String xml =
//                 "<Questions>\n" +
//                 "  <Question>\n" +
//                 "    <Category>Science</Category>\n" +
//                 "    <Value>100</Value>\n" +
//                 "    <QuestionText>What is H2O?</QuestionText>\n" +
//                 "    <Options>\n" +
//                 "      <OptionA>Hydrogen</OptionA>\n" +
//                 "      <OptionB>Oxygen</OptionB>\n" +
//                 "      <OptionC>Water</OptionC>\n" +
//                 "      <OptionD>Helium</OptionD>\n" +
//                 "    </Options>\n" +
//                 "    <CorrectAnswer>C</CorrectAnswer>\n" +
//                 "  </Question>\n" +
//                 "</Questions>\n";

//         Path temp = Files.createTempFile("jeopardy-valid", ".xml");
//         Files.writeString(temp, xml);

//         XmlGameDataLoader loader = new XmlGameDataLoader();
//         GameData data = loader.load(temp);

//         assertNotNull(data);
//         assertEquals(1, data.getTotalQuestions());
//         Files.deleteIfExists(temp);
//     }

//     @Test
//     void load_missingCategory_shouldFailValidation() throws Exception {
//         String xml =
//                 "<Questions>\n" +
//                 "  <Question>\n" +
//                 "    <!-- Missing <Category> -->\n" +
//                 "    <Value>100</Value>\n" +
//                 "    <QuestionText>What is H2O?</QuestionText>\n" +
//                 "    <Options>\n" +
//                 "      <OptionA>Hydrogen</OptionA>\n" +
//                 "      <OptionB>Oxygen</OptionB>\n" +
//                 "      <OptionC>Water</OptionC>\n" +
//                 "      <OptionD>Helium</OptionD>\n" +
//                 "    </Options>\n" +
//                 "    <CorrectAnswer>C</CorrectAnswer>\n" +
//                 "  </Question>\n" +
//                 "</Questions>\n";

//         Path temp = Files.createTempFile("jeopardy-invalid", ".xml");
//         Files.writeString(temp, xml);

//         XmlGameDataLoader loader = new XmlGameDataLoader();

//         Exception ex = assertThrows(
//                 Exception.class,
//                 () -> loader.load(temp)
//         );
//         assertTrue(
//                 ex.getMessage().toLowerCase().contains("category") ||
//                 ex.getMessage().toLowerCase().contains("missing"),
//                 "Message should mention missing Category"
//         );
//         Files.deleteIfExists(temp);
//     }
// }
