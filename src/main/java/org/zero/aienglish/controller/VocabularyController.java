package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zero.aienglish.entity.VocabularyEntity;
import org.zero.aienglish.model.UserPrincipal;
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
            @PathVariable Integer wordId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return vocabularyService.getWordForSentence(wordId, user.getId());
    }

    @GetMapping("/{wordId}")
    public VocabularyWord getVocabularyWord(
            @PathVariable Integer wordId,
            @AuthenticationPrincipal UserPrincipal user
            ) {
        return vocabularyService.getWordForVocabulary(wordId, user.getId());
    }

    @PutMapping("/{wordId}")
    public void saveWordToVocabulary(
            @PathVariable Integer wordId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {

        vocabularyService.saveWordToVocabulary(wordId, userPrincipal.getId());
    }

    @GetMapping
    public List<VocabularyEntity> getVocabularyWords(
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return vocabularyService.getVocabulary(userPrincipal.getId(), page);
    }
}
