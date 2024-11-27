package org.zero.aienglish.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zero.aienglish.model.SentenceDTO;
import org.zero.aienglish.model.VocabularySentence;

import static org.junit.jupiter.api.Assertions.*;

class SentenceMapperTest {
    private SentenceDTO sentenceDTO;
    private VocabularySentence vocabularySentence;
    private SentenceMapper sentenceMapper;

    @BeforeEach
    void setUp() {
        sentenceMapper = new SentenceMapperImpl();

        sentenceDTO = new SentenceDTO() {
            @Override
            public Integer getId() {
                return 3;
            }

            @Override
            public String getTranslate() {
                return "test translate";
            }

            @Override
            public String getSentence() {
                return "test sentence";
            }

            @Override
            public Double getScore() {
                return null;
            }
        };

        vocabularySentence = VocabularySentence.builder()
                .id(sentenceDTO.getId())
                .translation(sentenceDTO.getTranslate())
                .sentence(sentenceDTO.getSentence())
                .build();
    }

    @Test
    void sentenceDTOIntoVocabularySentence() {
        var res = sentenceMapper.map(sentenceDTO);

        Assertions.assertEquals(vocabularySentence.id(), res.id());
        Assertions.assertEquals(vocabularySentence.translation(), res.translation());
        Assertions.assertEquals(vocabularySentence.sentence(), res.sentence());
    }
}