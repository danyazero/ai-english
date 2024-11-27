package org.zero.aienglish.utils.taskGenerators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.mapper.WordMapper;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.SentenceRepository;
import org.zero.aienglish.repository.VocabularyRepository;
import org.zero.aienglish.repository.VocabularySentenceRepository;
import org.zero.aienglish.service.SentenceService;
import org.zero.aienglish.utils.Random;
import org.zero.aienglish.utils.SentenceCheck;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WordMeaningTaskGenerator implements TaskGenerator {
    private static final String[] speechPartIgnoreList = new String[]{"article", "preposition", "pronoun", "unknown", "gerund", "participle"};
    private final WordMapper wordMapper;
    private final SentenceCheck sentenceCheck;
    private final SentenceService sentenceService;
    private final VocabularySentenceRepository vocabularyRepository;

    @Override
    public TaskType getTaskName() {
        return TaskType.wordMeaning;
    }

    @Override
    public SentenceTask generateTask(SentenceDTO selectedSentence) {
        var sentenceTask = sentenceService.getSentenceDetails(selectedSentence);

        var relevantWordList = sentenceTask.words().stream().filter(WordMeaningTaskGenerator::isWordRelevant).toList();
        var randomWordIndex = Random.nextInRange(0, relevantWordList.size() - 1);
        var selectedWord = relevantWordList.get(randomWordIndex);
        var title = selectedWord.getTranslate();
        selectedWord.setTranslate(selectedSentence.getSentence());

        var answerSentences = vocabularyRepository.getRandomWordList(
                        3,
                        selectedWord.getWord(),
                        speechPartIgnoreList
                ).stream()
                .map(wordMapper::map).collect(Collectors.toList());
        answerSentences.add(selectedWord);
        Collections.shuffle(answerSentences);

        return SentenceTask.builder()
                .sentenceId(selectedSentence.getId())
                .title(title)
                .answers(answerSentences)
                .taskType(TaskType.wordMeaning)
                .build();
    }

    @Override
    public CheckResult checkTask(Integer userId, TaskResultDTO result) {
        log.info("task result: {}", result);
        result.wordList().getFirst().setWord(result.wordList().getFirst().getTranslate());
        return sentenceCheck.apply(userId, result);
    }

    private static boolean isWordRelevant(WordResponseDTO word) {
        return !Arrays.stream(speechPartIgnoreList).toList().contains(word.getSpeechPart().toLowerCase())
                || word.getWord().toLowerCase().charAt(0) != word.getWord().charAt(0);
    }

}
