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
public class GetVocabularyWithSpeechPart implements BiFunction<WordDTO, List<SpeechPart>, Vocabulary> {
    private final WordMapper wordMapper;
    private final SetSpeechPart setSpeechPart;

    @Override
    public Vocabulary apply(WordDTO currentWord, List<SpeechPart> speechPartList) {
        log.info(
                "Current word: {}, with speech part: {}, in list: {}",
                currentWord.word(),
                currentWord.speechPart(),
                speechPartList.stream().map(SpeechPart::getTitle).toList()
        );
        var currentSpeechPart = speechPartList.stream()
                .filter(speechPart -> speechPart.getTitle().equals(currentWord.speechPart()))
                .findFirst();
        if (currentSpeechPart.isEmpty()) {
            log.info("Speech part not found: {}", currentWord.speechPart());
            return null;
        };

        log.info("Speech part found: {}", currentWord.speechPart());
        var mappedWord = wordMapper.map(currentWord);
        return setSpeechPart.apply(mappedWord, currentSpeechPart.get());

    }
}
