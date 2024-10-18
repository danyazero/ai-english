package org.zero.aienglish.model;

import java.util.List;

public record SentenceRequest(
        SentenceDTO sentence,
        List<WordDTO> wordList
) {
}
