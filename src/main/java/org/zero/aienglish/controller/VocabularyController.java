package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.zero.aienglish.model.Pagination;
import org.zero.aienglish.model.UserWordDTO;
import org.zero.aienglish.model.VocabularyWord;
import org.zero.aienglish.service.VocabularyService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/vocabulary")
public class VocabularyController {
    private final VocabularyService vocabularyService;


/*
    @GetMapping({"/{value}"})
    public List<UserWordDTO> searchVocabulary(@PathVariable String value) {
        log.info("searchVocabulary");
        return vocabularyService.getVocabulariesByKey(value, 3);
    }
*/
    @GetMapping("/{wordId}")
    public VocabularyWord getVocabularyWord(
            @PathVariable Integer wordId,
            @RequestHeader(value = "User-Id") Integer userId
    ) {
        return vocabularyService.getVocabularyWord(userId, wordId);
    }

    @DeleteMapping("/{wordId}")
    public void deleteVocabularyWord(
            @PathVariable Integer wordId,
            @RequestHeader(value = "User-Id") Integer userId
    ) {
        log.info("Called Delete VocabularyWord -> {}", wordId);
        vocabularyService.deleteWordFromUserVocabulary(userId, wordId);
    }

    @PutMapping("/{wordId}")
    public void saveWordToVocabulary(
            @PathVariable Integer wordId,
            @RequestHeader(value = "User-Id") Integer userId
    ) {

        vocabularyService.saveWordToUserVocabulary(wordId, userId);
    }

    @GetMapping
    public Pagination getVocabularyWords(
            @RequestParam(defaultValue = "0", required = false) Integer page
    ) {
        log.info("Called Get VocabularyWords -> {}", page);
        return vocabularyService.getUserVocabulary(3, page);
    }
}
