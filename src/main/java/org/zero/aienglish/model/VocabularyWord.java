package org.zero.aienglish.model;

import lombok.Builder;

import java.util.List;

@Builder
public record VocabularyWord(
        String word,
        String translation,
        Boolean alreadySaved,
        List<VocabularySentence> sentences
) {
}
