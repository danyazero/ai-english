package org.zero.aienglish.mapper;

import org.zero.aienglish.entity.Sentence;

public class SentenceMapper {
    public static Sentence map(org.zero.aienglish.model.Sentence sentenceDTO) {
        return Sentence.builder()
                .sentence(sentenceDTO.sentence())
                .translation(sentenceDTO.translation())
                .explanation(sentenceDTO.explanation())
                .build();
    }
}
