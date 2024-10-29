package org.zero.aienglish.model;

import java.time.Instant;
import java.util.List;

public record SentenceResponse(
        String id,
        String object,
        Instant created,
        String model,
        List<ChoiceDTO> choices
) {
}
