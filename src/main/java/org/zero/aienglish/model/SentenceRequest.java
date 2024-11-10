package org.zero.aienglish.model;

import java.util.List;

public record SentenceRequest(
        Sentence sentence,
        List<WordDTO> wordList
) {
}
