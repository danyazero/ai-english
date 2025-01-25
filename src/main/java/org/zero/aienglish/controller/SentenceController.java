package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zero.aienglish.model.Sentence;
import org.zero.aienglish.service.SentenceService;

import java.util.List;

@RestController
@CrossOrigin(value = "http://localhost:5173")
@RequestMapping("/v1/api/sentence")
@RequiredArgsConstructor
public class SentenceController {
    private final SentenceService sentenceService;

    @PutMapping
    public void addSentence(@RequestBody List<Sentence> sentences) {
        for (Sentence sentence : sentences) {
            sentenceService.addSentence(sentence);
        }
    }
}
