package org.zero.aienglish.model;

public interface TaskGenerator {
    TaskType getTaskName();
//    SentenceTask generateTask(String word);
    SentenceTask generateTask(SentenceDTO selectedSentence);
    CheckResult checkTask(Integer userId, TaskResultDTO result);
}
