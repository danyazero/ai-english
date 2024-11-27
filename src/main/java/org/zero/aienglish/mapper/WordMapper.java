package org.zero.aienglish.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.zero.aienglish.entity.VocabularyEntity;
import org.zero.aienglish.entity.VocabularySentenceEntity;
import org.zero.aienglish.model.SentenceDTO;
import org.zero.aienglish.model.VocabularyDTO;
import org.zero.aienglish.model.WordDTO;
import org.zero.aienglish.model.WordResponseDTO;

@Mapper(builder = @Builder(disableBuilder = true))
public interface WordMapper {

    @Mapping(target = "speechPart", ignore = true)
    VocabularyEntity map(WordDTO wordDTO);

    @Mapping(target = "speechPart", ignore = true)
    WordDTO map(VocabularyEntity word);

//    @Mapping(target = "id", source = "vocabulary.id")
    @Mapping(target = "word", source = "defaultWord")
    @Mapping(target = "translate", source = "vocabulary.translate")
    @Mapping(target = "speechPart", source = "vocabulary.speechPart.title")
    WordResponseDTO map(VocabularySentenceEntity vocabulary);




    @Mapping(target = "speechPart", expression = "java(voc.getSpeechPart())")
    WordResponseDTO map(VocabularyDTO voc);

    @Mapping(target = "word", source = "sentence")
    @Mapping(target = "speechPart", constant = "Unknown")
    WordResponseDTO map(SentenceDTO sentence);
}
