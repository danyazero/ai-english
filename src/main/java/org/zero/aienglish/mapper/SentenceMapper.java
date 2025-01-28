package org.zero.aienglish.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.zero.aienglish.entity.Sentence;
import org.zero.aienglish.model.SentenceDTO;
import org.zero.aienglish.model.VocabularySentence;

@Mapper(builder = @Builder(disableBuilder = true))
public interface SentenceMapper {
    @Mapping(target = "id", ignore = true)
    Sentence map(org.zero.aienglish.model.Sentence sentenceDTO);
}
