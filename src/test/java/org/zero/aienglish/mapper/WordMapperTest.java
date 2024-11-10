package org.zero.aienglish.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.zero.aienglish.entity.SpeechPartEntity;
import org.zero.aienglish.entity.VocabularyEntity;
import org.zero.aienglish.entity.VocabularySentenceEntity;
import org.zero.aienglish.model.VocabularyDTO;

class WordMapperTest {
    private WordMapper wordMapper;
    private SpeechPartEntity speechPart;
    private VocabularyDTO vocabularyDTO;
    private VocabularySentenceEntity vocabularySentence;

    @BeforeEach
    void setUp() {
        wordMapper = new WordMapperImpl();

        speechPart = new SpeechPartEntity();
        speechPart.setId(null);
        speechPart.setTitle("Noun");
        speechPart.setAnswersTo("test answers to");
        speechPart.setTranslate("test translate");

        var vocabulary = new VocabularyEntity();
        vocabulary.setId(2);
        vocabulary.setWord("Test");
        vocabulary.setTranslate("Translate");
        vocabulary.setSpeechPart(speechPart);

        vocabularySentence = new VocabularySentenceEntity();
        vocabularySentence.setSentence(null);
        vocabularySentence.setVocabulary(vocabulary);
        vocabularySentence.setDefaultWord("Default word");
        vocabularySentence.setIsMarker(true);

        vocabularyDTO = new VocabularyDTO() {
            @Override
            public Integer getId() {
                return vocabulary.getId();
            }

            @Override
            public String getWord() {
                return vocabulary.getWord();
            }

            @Override
            public String getTranslate() {
                return vocabulary.getTranslate();
            }

            @Override
            public String getSpeechPart() {
                return speechPart.getTitle();
            }

            @Override
            public String getSpeechPartTranslate() {
                return speechPart.getTranslate();
            }

            @Override
            public String getAnswersTo() {
                return speechPart.getAnswersTo();
            }
        };
    }

    @Test
    @DisplayName("Map vocabularySentence into WordResponseDTO")
    void map() {
        var res = wordMapper.map(vocabularySentence);

        Assertions.assertEquals(speechPart, res.getSpeechPart());
        Assertions.assertEquals(vocabularySentence.getDefaultWord(), res.getWord());
        Assertions.assertEquals(vocabularySentence.getVocabulary().getTranslate(), res.getTranslate());
        Assertions.assertEquals(vocabularySentence.getIsMarker(), res.getIsMarker());
        Assertions.assertEquals(vocabularySentence.getVocabulary().getId(), res.getId());
    }


    @Test
    @DisplayName("Map vocabularyDTO into WordResponseDTO")
    void map_2() {
        var res = wordMapper.map(vocabularyDTO);

        Assertions.assertEquals(speechPart.getTitle(), res.getSpeechPart().getTitle());
        Assertions.assertEquals(speechPart.getAnswersTo(), res.getSpeechPart().getAnswersTo());
        Assertions.assertEquals(speechPart.getTranslate(), res.getSpeechPart().getTranslate());
        Assertions.assertEquals(vocabularySentence.getVocabulary().getWord(), res.getWord());
        Assertions.assertEquals(vocabularySentence.getVocabulary().getTranslate(), res.getTranslate());
        Assertions.assertEquals(false, res.getIsMarker());
        Assertions.assertEquals(vocabularySentence.getVocabulary().getId(), res.getId());
    }
}