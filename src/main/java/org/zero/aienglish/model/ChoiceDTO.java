package org.zero.aienglish.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChoiceDTO(
        @JsonProperty("index")
        Integer id,
        MessageDTO<List<SentenceDTO>> message,
        @JsonProperty("finish_reason")
        String finishReason
) {
}
