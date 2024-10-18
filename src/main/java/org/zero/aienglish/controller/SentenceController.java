package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zero.aienglish.entity.SpeechPart;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.model.SentenceRequest;
import org.zero.aienglish.service.SentenceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/sentence")
public class SentenceController {
    private final SentenceService sentenceService;

    @PutMapping
    public void addSentence(@RequestBody SentenceRequest sentenceRequest) {
       sentenceService.addSentence(sentenceRequest.sentence(), sentenceRequest.wordList());
    }

}
