package org.zero.aienglish.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record GenerateRequest(
        String model,
        List<MessageDTO<String>> messages,
        @JsonProperty("response_format")
        String responseFormat
) {
}
