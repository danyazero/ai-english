package org.zero.aienglish.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.model.WordDTO;

import java.util.List;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class WordNotExist implements BiFunction<List<Vocabulary>, WordDTO, Boolean> {
    @Override
    public Boolean apply(List<Vocabulary> alreadyExistedWords, WordDTO currentWord) {
        return alreadyExistedWords.stream()
                        .noneMatch(element ->
                                element.getWord().equalsIgnoreCase(currentWord.getWord())
                                        && element.getSpeechPart().getTitle().equalsIgnoreCase(currentWord.getSpeechPart()));
    }
}
