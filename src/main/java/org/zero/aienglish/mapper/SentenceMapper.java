package org.zero.aienglish.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.zero.aienglish.entity.SentenceEntity;
import org.zero.aienglish.model.Sentence;
import org.zero.aienglish.model.SentenceDTO;
import org.zero.aienglish.model.VocabularySentence;

@Mapper(builder = @Builder(disableBuilder = true))
public interface SentenceMapper {

    Sentence map(SentenceEntity sentence);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "views", defaultValue = "0", ignore = true)
    SentenceEntity map(Sentence sentenceDTO);

    @Mapping(target = "translation", source = "translate")
    VocabularySentence map(SentenceDTO sentence);
}
