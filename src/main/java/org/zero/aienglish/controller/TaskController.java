package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zero.aienglish.model.CheckResult;
import org.zero.aienglish.model.SentenceRequest;
import org.zero.aienglish.model.SentenceTask;
import org.zero.aienglish.model.TaskResultDTO;
import org.zero.aienglish.service.SentenceService;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/v1/api/task")
public class TaskController {
    private final SentenceService sentenceService;

    @GetMapping
    public SentenceTask getSentenceTask() {
        return sentenceService.getSentenceTask(3);
    }

    @PostMapping
    public CheckResult checkTask(@RequestBody TaskResultDTO taskResult) {
        return sentenceService.checkTask(3, taskResult);
    }

    @PutMapping
    public void addSentence(@RequestBody SentenceRequest sentenceRequest) {
//       sentenceService.addSentence(sentenceRequest.words(), sentenceRequest.wordList());
    }

}
