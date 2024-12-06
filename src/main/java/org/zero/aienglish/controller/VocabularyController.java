package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.model.VocabularyWord;
import org.zero.aienglish.service.VocabularyService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/vocabulary")
public class VocabularyController {
    private final VocabularyService vocabularyService;

    @GetMapping("/sentence/{wordId}")
    public VocabularyWord getSentenceVocabularyWord(
            @PathVariable Integer wordId
    ) {
        return vocabularyService.getWordForSentence(wordId, 3);
    }

    @GetMapping("/{wordId}")
    public VocabularyWord getVocabularyWord(
            @PathVariable Integer wordId
            ) {
        return vocabularyService.getWordForVocabulary(wordId, 3);
    }

    @PutMapping("/{wordId}")
    public void saveWordToVocabulary(
            @PathVariable Integer wordId
    ) {

        vocabularyService.saveWordToVocabulary(wordId, 3);
    }

    @GetMapping
    public List<Vocabulary> getVocabularyWords(
            @RequestParam(defaultValue = "0", required = false) Integer page
    ) {
        return vocabularyService.getVocabulary(3, page);
    }
}
