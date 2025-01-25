package org.zero.aienglish.model;

import lombok.Builder;
import org.zero.aienglish.entity.Sentence;
import org.zero.aienglish.entity.VocabularySentence;

import java.util.List;

@Builder
public record VocabularyWord(
        String word,
        String translation,
        Boolean alreadySaved,
        String meaning,
        List<Sentence> sentences
) {
}
