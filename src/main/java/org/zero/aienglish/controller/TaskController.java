package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zero.aienglish.model.CheckResult;
import org.zero.aienglish.model.SentenceTask;
import org.zero.aienglish.model.TaskResultDTO;
import org.zero.aienglish.service.TaskService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/task")
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public SentenceTask getSentenceTask() {
        return taskService.getTask(3);
    }

    @PostMapping
    public CheckResult checkTask(@RequestBody TaskResultDTO taskResult) {
        return taskService.checkTask(3, taskResult);
    }
}
