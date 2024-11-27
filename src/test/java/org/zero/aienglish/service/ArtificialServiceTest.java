package org.zero.aienglish.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.client.RestTemplate;
import org.zero.aienglish.config.OpenAIConfig;
import org.zero.aienglish.exception.SentenceUpdateException;
import org.zero.aienglish.model.*;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtificialServiceTest {
    @Mock
    private OpenAIConfig config;
    @Mock
    private SentenceService sentenceService;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private Validator validator;
    @InjectMocks
    private ArtificialService artificialService;

    HttpHeaders headers;
    private String apiKey;
    private String apiModel;
    private String apiURL;
    private ThemeDTO moneyHeistTheme;
    private ResponseEntity<SentenceResponse> responseEntity;
    private ResponseEntity<SentenceResponse> errorEntity;
    private ResponseEntity<SentenceResponse> lengthErrorResponseEntity;

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        apiKey = "01JB4251GG5VJSQAAEVMD99QHP";
        apiModel = "gpt4o";
        apiURL = "http://localhost:8888/api/generate";

        List<WordDTO> vocabulary = getVocabulary();

        var sentence = new Sentence(
                "Amenadiel helps Lucifer understand his emotions.",
                "Аменадіель допомагає Люциферу зрозуміти свої емоції.",
                new String[]{"Present Simple"},
                vocabulary
        );

        var message = new MessageDTO<>("assistant", List.of(sentence));
        ChoiceDTO choice_1 = new ChoiceDTO(0, message, "stop");

        var choice_length = new ChoiceDTO(1, null, "length");

        var responseBody = new SentenceResponse(
                "01JB43K4YYB436CDMX22ZNS868",
                "chat.completion",
                Instant.now(),
                apiModel,
                List.of(choice_1)
        );

        var lengthErrorResponseBody = new SentenceResponse(
                "01JB43K4YYB436CDMX22ZNS868",
                "chat.completion",
                Instant.now(),
                apiModel,
                List.of(choice_length)
        );

        headers.add("Authorization", "Bearer " + apiKey);

        responseEntity = new ResponseEntity<SentenceResponse>(responseBody, HttpStatus.ACCEPTED);
        lengthErrorResponseEntity = new ResponseEntity<>(lengthErrorResponseBody, HttpStatus.ACCEPTED);
        errorEntity = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        moneyHeistTheme = getMoneyHeistTheme();
    }

    @Test
    void generateSentenceListByTheme() {
        when(config.getUrl()).thenReturn(apiURL);
        when(config.getModel()).thenReturn(apiModel);
        when(config.getKey()).thenReturn(apiKey);

        var generatedRequestBody = ArtificialService.getRequestForGenerate(
                moneyHeistTheme,
                3,
                apiModel
        );

        var entity = new HttpEntity<>(generatedRequestBody, headers);
        when(restTemplate.postForEntity(apiURL, entity, SentenceResponse.class))
                .thenReturn(responseEntity);
        var res = artificialService.generateSentenceListByTheme(moneyHeistTheme);
        var vocabularyList = responseEntity.getBody().choices().getFirst().message().content();

        Assertions.assertEquals(vocabularyList.size(), res.size());
        Assertions.assertEquals(vocabularyList.getFirst(), res.getFirst());
        Assertions.assertEquals(vocabularyList.getFirst().sentence(), res.getFirst().sentence());
        Assertions.assertEquals(vocabularyList.getFirst().translation(), res.getFirst().translation());
        Assertions.assertEquals(vocabularyList.getFirst().vocabulary().size(), res.getFirst().vocabulary().size());
    }

    @Test
    void generateSentenceListByTheme_withGenerationError() {
        when(config.getUrl()).thenReturn(apiURL);
        when(config.getModel()).thenReturn(apiModel);
        when(config.getKey()).thenReturn(apiKey);

        var generatedRequestBody = ArtificialService.getRequestForGenerate(
                moneyHeistTheme,
                3,
                apiModel
        );

        var entity = new HttpEntity<>(generatedRequestBody, headers);
        when(restTemplate.postForEntity(apiURL, entity, SentenceResponse.class))
                .thenReturn(lengthErrorResponseEntity);
        Assertions.assertThrows(SentenceUpdateException.class, () -> artificialService.generateSentenceListByTheme(moneyHeistTheme));
    }

    @Test
    void generateSentenceListByTheme_withGenerationRuntimeError() {
        when(config.getUrl()).thenReturn(apiURL);
        when(config.getModel()).thenReturn(apiModel);
        when(config.getKey()).thenReturn(apiKey);

        var generatedRequestBody = ArtificialService.getRequestForGenerate(
                moneyHeistTheme,
                3,
                apiModel
        );

        var entity = new HttpEntity<>(generatedRequestBody, headers);
        when(restTemplate.postForEntity(apiURL, entity, SentenceResponse.class))
                .thenReturn(errorEntity);
        Assertions.assertThrows(
                SentenceUpdateException.class,
                () -> artificialService.generateSentenceListByTheme(moneyHeistTheme)
        );
    }


    private static ThemeDTO getMoneyHeistTheme() {
        return new ThemeDTO() {
            @Override
            public Integer getId() {
                return 0;
            }

            @Override
            public String getTitle() {
                return "Money Heist";
            }

            @Override
            public Integer getYear() {
                return 2020;
            }
        };
    }

    private static List<WordDTO> getVocabulary() {
        return List.of(
                new WordDTO("Amenadiel", (short) 0, "Amenadiel", "Аменадіель", "noun", false),
                new WordDTO("helps", (short) 1, "help", "допомагає", "verb", true),
                new WordDTO("Lucifer", (short) 2, "Lucifer", "Люцифер", "noun", false),
                new WordDTO("understand", (short) 3, "understand", "зрозуміти", "verb", false),
                new WordDTO("his", (short) 4, "his", "свої", "pronoun", false),
                new WordDTO("emotions", (short) 5, "emotion", "емоції", "noun", false)
        );
    }
}