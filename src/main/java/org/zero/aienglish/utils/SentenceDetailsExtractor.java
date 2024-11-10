package org.zero.aienglish.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.SentenceTenseEntity;
import org.zero.aienglish.mapper.WordMapper;
import org.zero.aienglish.model.SentenceDTO;
import org.zero.aienglish.model.TaskDTO;
import org.zero.aienglish.repository.SentenceTenseRepository;
import org.zero.aienglish.repository.VocabularySentenceRepository;

import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SentenceDetailsExtractor implements Function<SentenceDTO, TaskDTO> {
    private final VocabularySentenceRepository vocabularySentenceRepository;
    private final SentenceTenseRepository sentenceTenseRepository;
    private final WordMapper wordMapper;

    @Override
    public TaskDTO apply(SentenceDTO selectedSentence) {
            var tenseList = sentenceTenseRepository.findAllBySentenceId(selectedSentence.getId())
                    .stream()
                    .map(SentenceTenseEntity::getTense)
                    .toList();
            log.info("For sentence with id -> {}, successfully retrieved tense list with size -> {}",
                    selectedSentence.getId(), tenseList.size());

            var vocabulary = vocabularySentenceRepository.getAllBySentenceIdOrderByOrder(selectedSentence.getId())
                    .stream().map(wordMapper::map).collect(Collectors.toList());
            log.info("For sentence with id -> {}, successfully retrieved vocabulary list with size -> {}",
                    selectedSentence.getId(), vocabulary.size());

            return TaskDTO.builder()
                    .id(selectedSentence.getId())
                    .translate(selectedSentence.getTranslate())
                    .tenses(tenseList)
                    .words(vocabulary)
                    .build();
    }
}
