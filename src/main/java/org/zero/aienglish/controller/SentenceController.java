package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.aienglish.entity.SpeechPart;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.service.SentenceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/sentence")
public class SentenceController {
    private final SentenceService sentenceService;
}
