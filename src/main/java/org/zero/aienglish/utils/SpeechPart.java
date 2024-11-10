package org.zero.aienglish.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.SpeechPartEntity;
import org.zero.aienglish.entity.VocabularyEntity;
import org.zero.aienglish.mapper.WordMapper;
import org.zero.aienglish.model.WordDTO;
import org.zero.aienglish.repository.SpeechRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpeechPart implements BiFunction<WordDTO, List<SpeechPartEntity>, VocabularyEntity> {
    private final WordMapper wordMapper;
    private final SetSpeechPart setSpeechPart;
    private final SpeechRepository speechRepository;

    @Override
    public VocabularyEntity apply(WordDTO currentWord, List<SpeechPartEntity> speechPartList) {
        log.info(
                "Current word: {}, with speech part: {}",
                currentWord.getDefaultWord(),
                currentWord.getSpeechPart()
        );

        var currentSpeechPart = getSpeechPartOrReplaceWithUnknown(speechPartList, currentWord);

        if (currentSpeechPart.isEmpty()) return null;

        log.info("Speech part found: {}", currentSpeechPart.get().getTitle());

        var mappedWord = wordMapper.map(currentWord);
        return setSpeechPart.apply(mappedWord, currentSpeechPart.get());
    }

    private Optional<SpeechPartEntity> getSpeechPartOrReplaceWithUnknown(List<SpeechPartEntity> speechPartList, WordDTO currentWord) {
        var currentSpeechPart = speechPartList.stream()
                .filter(speechPart -> isTitleEquals(currentWord, speechPart))
                .findFirst()
                .or(() -> {
                    log.info("Speech part not found: {} -> replacing with unknown speech part", currentWord.getSpeechPart());
                    return Optional.ofNullable(speechRepository.getReferenceById(1));
                });

        return currentSpeechPart
                .or(() -> {
                    log.warn("Something went wrong. Speech part is still null");
                    return Optional.empty();
                });
    }

    private static boolean isTitleEquals(WordDTO currentWord, SpeechPartEntity speechPart) {
        return speechPart.getTitle().equalsIgnoreCase(currentWord.getSpeechPart());
    }
}
