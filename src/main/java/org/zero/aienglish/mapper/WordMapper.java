package org.zero.aienglish.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.entity.VocabularySentence;
import org.zero.aienglish.model.SentenceDTO;
import org.zero.aienglish.model.VocabularyDTO;
import org.zero.aienglish.model.WordDTO;
import org.zero.aienglish.model.WordResponseDTO;

@Mapper(builder = @Builder(disableBuilder = true))
public interface WordMapper {

    Vocabulary map(WordDTO wordDTO);

    WordDTO map(Vocabulary word);

    @Mapping(target = "word", source = "defaultWord")
    @Mapping(target = "translate", source = "vocabulary.translate")
    WordResponseDTO map(VocabularySentence vocabulary);




    WordResponseDTO map(VocabularyDTO voc);

    @Mapping(target = "word", source = "sentence")
    WordResponseDTO map(SentenceDTO sentence);
}
