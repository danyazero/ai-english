package org.zero.aienglish.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zero.aienglish.entity.*;
import org.zero.aienglish.mapper.WordMapper;
import org.zero.aienglish.model.SentenceDTO;
import org.zero.aienglish.model.TaskDTO;
import org.zero.aienglish.model.WordResponseDTO;
import org.zero.aienglish.repository.SentenceTenseRepository;
import org.zero.aienglish.repository.VocabularySentenceRepository;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SentenceDetailsExtractorTest {
    @Mock
    private VocabularySentenceRepository vocabularySentenceRepository;
    @Mock
    private SentenceTenseRepository sentenceTenseRepository;
    @Mock
    private WordMapper wordMapper;
    @InjectMocks
    private SentenceDetailsExtractor sentenceDetailsExtractor;

    private SentenceDTO sentenceDTO;
    private SentenceTenseEntity sentenceTense;
    private VocabularyEntity vocabulary;
    private VocabularySentenceEntity vocabularySentence;
    private WordResponseDTO word1;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        sentenceDTO = new SentenceDTO() {
            @Override
            public Integer getId() {
                return 2;
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
                return 98.0;
            }
        };
        var sentence = SentenceEntity.builder()
                .id(sentenceDTO.getId())
                .sentence(sentenceDTO.getSentence())
                .translation(sentenceDTO.getTranslate())
                .build();

        var time = TimeEntity.builder()
                .id(2)
                .title("Present")
                .build();

        var duration = DurationEntity.builder()
                .id(5)
                .title("Simple")
                .build();

        var tense = TenseEntity.builder()
                .titleTime(time)
                .titleDuration(duration)
                .verb("test")
                .formula("test")
                .build();

        sentenceTense = SentenceTenseEntity.builder()
                .sentence(sentence)
                .tense(tense)
                .build();
        word1 = WordResponseDTO.builder()
                .word("test")
                .translate("test translate")
                .build();
        taskDTO = TaskDTO.builder()
                .id(2)
                .words(List.of(word1))
                .tenses(List.of(tense))
                .build();

        vocabulary = VocabularyEntity.builder()
                .word("test")
                .translate("test translate")
                .build();

        vocabularySentence = VocabularySentenceEntity.builder()
                .order((short) 0)
                .sentence(sentence)
                .vocabulary(vocabulary)
                .isMarker(false)
                .defaultWord("test")
                .build();
    }

    @Test
    void sentenceDetailsExtractor() {
        when(sentenceTenseRepository.findAllBySentenceId(sentenceDTO.getId())).thenReturn(List.of(sentenceTense));
        when(vocabularySentenceRepository.getAllBySentenceIdOrderByOrder(sentenceDTO.getId())).thenReturn(List.of(vocabularySentence));
        when(wordMapper.map(vocabularySentence)).thenReturn(word1);
        var res = sentenceDetailsExtractor.apply(sentenceDTO);

        Assertions.assertEquals(taskDTO.id(), res.id());
        Assertions.assertEquals(taskDTO.words(), res.words());
        Assertions.assertEquals(taskDTO.tenses(), res.tenses());
        Assertions.assertEquals(taskDTO.sentence(), res.sentence());
    }
}