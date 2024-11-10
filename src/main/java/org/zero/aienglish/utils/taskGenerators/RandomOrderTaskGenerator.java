package org.zero.aienglish.utils.taskGenerators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.mapper.TenseMapper;
import org.zero.aienglish.model.*;
import org.zero.aienglish.utils.SentenceCheck;
import org.zero.aienglish.utils.SentenceDetailsExtractor;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class RandomOrderTaskGenerator implements TaskGenerator {
    private final TenseMapper tenseMapper;
    private final SentenceCheck sentenceCheck;
    private final SentenceDetailsExtractor sentenceDetailsExtractor;

    @Override
    public TaskType getTaskName() {
        return TaskType.randomOrder;
    }

    @Override
    public SentenceTask generateTask(SentenceDTO selectedSentence) {
        var sentence = sentenceDetailsExtractor.apply(selectedSentence);
        var tenses = sentence.tenses().stream().map(tenseMapper::map).toList();
        Collections.shuffle(sentence.words());

        return SentenceTask.builder()
                .taskType(this.getTaskName())
                .words(Collections.emptyList())
                .title(sentence.translate())
                .sentenceId(sentence.id())
                .answers(sentence.words())
                .tenses(tenses)
                .build();
    }

    @Override
    public CheckResult checkTask(Integer userId, TaskResultDTO result) {
        return sentenceCheck.apply(userId, result);
    }
}
