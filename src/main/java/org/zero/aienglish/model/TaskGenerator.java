package org.zero.aienglish.model;

import lombok.extern.slf4j.Slf4j;
import org.zero.aienglish.entity.Sentence;
import org.zero.aienglish.repository.SentenceHistoryRepository;
import org.zero.aienglish.repository.StatusRepository;

@Slf4j
public abstract class TaskGenerator {
    abstract public TaskType getTaskName();
    abstract public SentenceTask generateTask(SentenceDTO selectedSentence);
    abstract public CheckResult checkTask(Integer userId, String result, Sentence sentence);

    protected void saveTaskLog(
            boolean isCorrect,
            Integer userId,
            StatusRepository statusRepository,
            SentenceHistoryRepository sentenceHistoryRepository
    ) {
        var statusReference = statusRepository.getReferenceById(isCorrect ? 2 : 3);
        var logRow = sentenceHistoryRepository.findFirstByUser_IdAndStatus_IdOrderByAtDesc(userId, 1);
        if (logRow.isEmpty()) {
            log.warn("Log row not founded for user -> {}", userId);
            return;
        }
        logRow.get().setStatus(statusReference);

        sentenceHistoryRepository.save(logRow.get());
    }
}
