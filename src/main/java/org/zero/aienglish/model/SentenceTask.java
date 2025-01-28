package org.zero.aienglish.model;

import lombok.Builder;

import java.util.List;

@Builder
public record SentenceTask(
        Integer sentenceId,
        TaskType taskType,
        String title,
        String caption,
        String pattern,
        Integer stepAmount,
        Integer currentStep,
        List<TaskAnswer> answers
) {
    public SentenceTask changeTitleAndAnswers(String title, List<TaskAnswer> answers) {
        return SentenceTask.builder()
                .currentStep(this.currentStep)
                .stepAmount(this.stepAmount)
                .sentenceId(this.sentenceId)
                .taskType(this.taskType)
                .caption(this.caption)
                .pattern(this.pattern)
                .answers(answers)
                .title(title)
                .build();
    }
}
