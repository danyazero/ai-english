package org.zero.aienglish.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.SpeechPart;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.mapper.WordMapper;
import org.zero.aienglish.model.WordDTO;

import java.util.List;
import java.util.function.BiFunction;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithSpeechPart implements BiFunction<WordDTO, List<SpeechPart>, Vocabulary> {
    private final WordMapper wordMapper;
    private final SetSpeechPart setSpeechPart;

    @Override
    public Vocabulary apply(WordDTO currentWord, List<SpeechPart> speechPartList) {
        log.info(
                "Current word: {}, with speech part: {}",
                currentWord.getWord(),
                currentWord.getSpeechPart()
        );
        var currentSpeechPart = speechPartList.stream()
                .filter(speechPart -> isTitleEquals(currentWord, speechPart))
                .findFirst();

        if (currentSpeechPart.isEmpty()) {
            log.info("Speech part not found: {}", currentWord.getSpeechPart());
            return null;
        };

        log.info("Speech part found: {}", currentWord.getSpeechPart());
        var mappedWord = wordMapper.map(currentWord);
        return setSpeechPart.apply(mappedWord, currentSpeechPart.get());

    }

    private static boolean isTitleEquals(WordDTO currentWord, SpeechPart speechPart) {
        return speechPart.getTitle().equalsIgnoreCase(currentWord.getSpeechPart());
    }
}
