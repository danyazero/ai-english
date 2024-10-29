package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.zero.aienglish.config.OpenAIConfig;
import org.zero.aienglish.exception.SentenceUpdateException;
import org.zero.aienglish.model.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtificialService {
    private static final String AI_RESULT_CHOICE = "assistant";
    private static final String AI_STATUS_STOP = "stop";
    private final SentenceService sentenceService;
    private final OpenAIConfig config;
    private final RestTemplate api;

    public Integer generateSentenceListByTheme(ThemeDTO theme) {
        return generateSentenceListByTheme(theme, 3);
    }

    public Integer generateSentenceListByTheme(
            ThemeDTO theme,
            Integer sentenceCount
    ) {
        var headers = getHttpHeaders();
        var requestForGenerate = getRequestForGenerate(
                theme,
                sentenceCount,
                config.getModel()
        );

        var entity = new HttpEntity<>(requestForGenerate, headers);
        var responseEntity = api.postForEntity(
                config.getUrl(),
                entity, SentenceResponse.class
        );

        HttpStatusCode statusCode = responseEntity.getStatusCode();
        if (!statusCode.is2xxSuccessful() || isGeneratedSuccessfully(responseEntity)) {
            log.warn("Sentence Update Error occurred with status code -> {}", statusCode);
            throw new SentenceUpdateException("Sentence Update Error");
        }

        SentenceResponse body = getRequestBodyOrThrow(responseEntity);

        return saveAllSentenceList(body, theme.getId());
    }

    public static GenerateRequest getRequestForGenerate(
            ThemeDTO theme,
            Integer sentenceCount,
            String apiModel
    ) {
        var messageList = getMessageList(theme, sentenceCount);

        return GenerateRequest.builder()
                .model(apiModel)
                .messages(messageList)
                .responseFormat(getResultSchema())
                .build();
    }

    private static SentenceResponse getRequestBodyOrThrow(
            ResponseEntity<SentenceResponse> responseEntity
    ) {
        SentenceResponse body = responseEntity.getBody();
        if (isChoicesEmpty(body)) {
            log.warn("Nothing has been provided for update");
            throw new SentenceUpdateException("Nothing has been provided for update");
        }
        return body;
    }

    private static List<MessageDTO<String>> getMessageList(ThemeDTO theme, Integer sentenceCount) {
        return List.of(
                getSystemMessage(sentenceCount),
                getUserMessage(theme.getTitle())
        );
    }

    private HttpHeaders getHttpHeaders() {
        var headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + config.getKey());
        return headers;
    }

    private Integer saveAllSentenceList(
            SentenceResponse body,
            Integer themeId
    ) {
        List<SentenceDTO> sentenceList = getGeneratedContent(body);
        var successfullySaved = 0;
        for (SentenceDTO sentence : sentenceList) {
            try {
                sentenceService.addSentence(sentence, themeId);
                successfullySaved++;
            } catch (Exception e) {
                log.warn("Error occurred while adding sentence -> {}", e.getMessage());
            }
        }
        return successfullySaved;
    }

    private static MessageDTO<String> getUserMessage(String theme) {
        return new MessageDTO<>("user", "I want to practice the theme of the \"" + theme + "\" series. ");
    }

    private static MessageDTO<String> getSystemMessage(Integer sentenceCount) {
        return new MessageDTO<>(
                "system",
                "You are a helpful English tutor. Choose a "
                        + sentenceCount +
                        " sentences for the user to practice on a topic on their choice."
        );
    }

    private static boolean isChoicesEmpty(SentenceResponse body) {
        return Objects.requireNonNull(body).choices() == null || body.choices().isEmpty();
    }

    private static List<SentenceDTO> getGeneratedContent(SentenceResponse body) {
        return body.choices().stream()
                .filter(ArtificialService::isGenerationResultChoice)
                .findFirst()
                .orElseThrow(() -> new SentenceUpdateException("Generation result not found"))
                .message()
                .content();
    }

    private static boolean isGeneratedSuccessfully(ResponseEntity<SentenceResponse> responseEntity) {
        return !Objects.equals(
                getGenerationFinishReason(responseEntity),
                AI_STATUS_STOP
        );
    }

    private static boolean isGenerationResultChoice(ChoiceDTO choice) {
        return choice.message().role().equalsIgnoreCase(AI_RESULT_CHOICE);
    }

    private static String getGenerationFinishReason(ResponseEntity<SentenceResponse> responseEntity) {
        return Objects.requireNonNull(responseEntity.getBody()).choices().getFirst().finishReason();
    }

    private static String getResultSchema() {
        return """
            {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "sentence": {
                    "type": "string"
                  },
                  "translation": {
                    "type": "string",
                    "description": "Given sentence translated into Ukrainian language"
                  },
                  "sentenceTense": {
                    "type": "array",
                    "items": {
                      "type": "string",
                      "description": "Tenses used in this sentence"
                    },
                    "additionalProperties": false
                  },
                  "vocabulary": {
                    "type": "array",
                    "items": {
                      "type": "object",
                      "properties": {
                        "order": {
                          "type": "number",
                          "description": "Order of this word in sentence (from 0 to n)"
                        },
                        "word": {
                          "type": "string"
                        },
                        "defaultWord": {
                          "type": "string",
                          "description": "Word default form for instance looking -> look"
                        },
                        "translate": {
                          "type": "string",
                          "description": "Word translated into Ukrainian language"
                        },
                        "speechPart": {
                          "type": "string",
                          "description": "For instance it can be: verb, noun, auxiliary verb"
                        },
                        "isMarker": {
                          "type": "boolean",
                          "description": "Whether the word is an indicator of the tense used in a given sentence"
                        }
                      },
                      "required": [
                        "order",
                        "word",
                        "defaultWord",
                        "translate",
                        "speechPart",
                        "isMarker"
                      ],
                      "additionalProperties": false
                    }
                  }
                },
                "required": [
                  "sentence",
                  "translation",
                  "sentenceTenses",
                  "vocabulary"
                ],
                "additionalProperties": false
              },
              "additionalProperties": false
            }
        """;
    }
}
