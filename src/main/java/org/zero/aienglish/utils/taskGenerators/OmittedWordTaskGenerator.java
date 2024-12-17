package org.zero.aienglish.utils.taskGenerators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zero.aienglish.entity.Sentence;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.mapper.TenseMapper;
import org.zero.aienglish.mapper.WordMapper;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.SentenceHistoryRepository;
import org.zero.aienglish.repository.StatusRepository;
import org.zero.aienglish.repository.VocabularySentenceRepository;
import org.zero.aienglish.service.SentenceService;
import org.zero.aienglish.utils.AccuracyCheck;
import org.zero.aienglish.utils.Random;
import org.zero.aienglish.utils.SentenceCheck;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor

public class OmittedWordTaskGenerator extends TaskGenerator {
    private static final String[] speechPartIgnoreList = new String[]{"article", "preposition", "pronoun", "unknown", "gerund", "participle"};
    private final SentenceHistoryRepository sentenceHistoryRepository;
    private final VocabularySentenceRepository vocabularyRepository;
    private final StatusRepository statusRepository;
    private final SentenceService sentenceService;
    private final AccuracyCheck accuracyCheck;
    private final TenseMapper tenseMapper;
    private final WordMapper wordMapper;

    @Override
    public TaskType getTaskName() {
        return TaskType.omittedWord;
    }

    @Override
    @Transactional
    public SentenceTask generateTask(SentenceDTO selectedSentence) {
        var sentenceTask = sentenceService.getSentenceDetails(selectedSentence);
        log.info("Sentence details: {}", sentenceTask);


        var omittedWord = getOmittedWord(sentenceTask.words());
        var formattedSentence = getFormattedSentence(sentenceTask.sentence(), omittedWord);

        log.info(
                "For words with id -> {}, successfully omitted word -> {}",
                sentenceTask.id(), omittedWord.getWord()
        );

        var answerWordList = vocabularyRepository.getRandomWordList(
                        3,
                        omittedWord.getWord(),
                        speechPartIgnoreList
                ).stream()
                .map(wordMapper::map).collect(Collectors.toList());

        answerWordList.add(omittedWord);
        Collections.shuffle(answerWordList);

        log.info("Generated answer word list with size -> {}", answerWordList.size());

        var mappedTenses = sentenceTask.tenses()
                .stream()
                .map(tenseMapper::map)
                .toList();

        return SentenceTask.builder()
                .taskType(this.getTaskName())
                .sentenceId(sentenceTask.id())
                .title(formattedSentence)
                .answers(answerWordList)
                .tenses(mappedTenses)
                .build();
    }

    @Override
    public CheckResult checkTask(Integer userId, String result, Sentence sentence) {
        var checkResult = accuracyCheck.apply(sentence.getSentence(), result);
        this.saveTaskLog(checkResult > 98F, userId, statusRepository, sentenceHistoryRepository);

        return CheckResult.builder()
                .correctAnswer(sentence.getSentence())
                .accepted(checkResult > 98F)
                .mark(checkResult)
                .build();
    }

    private WordResponseDTO getOmittedWord(List<WordResponseDTO> vocabulary) {
        var randomWordIndex = Random.nextInRange(0, vocabulary.size() - 1);
        log.info("Generated next random int -> {}", randomWordIndex);
        var omittedWordId = vocabulary.get(randomWordIndex).getId();
        log.info("Omitted word id -> {}", omittedWordId);


        return vocabulary.stream()
                .filter(isOmittedWord(omittedWordId))
                .findFirst()
                .orElseThrow(
                        () -> new RequestException("Omitted word not found.")
                );
    }

    @NotNull
    private String getFormattedSentence(String sentence, WordResponseDTO omittedWord) {
        var splitedSentence = sentence.split(" ");
        for (int i = 0; i < splitedSentence.length; i++) {
            if (accuracyCheck.apply(omittedWord.getWord(), splitedSentence[i]) > 90) {
                splitedSentence[i] = "__";
                break;
            }
        }

        return String.join(" ", splitedSentence);
    }

    @NotNull
    private static Predicate<WordResponseDTO> isOmittedWord(Integer omittedWordId) {
        return word -> word.getId().equals(omittedWordId);
    }

    private static boolean isWordRelevant(WordResponseDTO word) {
        return !Arrays.stream(speechPartIgnoreList).toList()
                .contains(
                        word.getSpeechPart().toLowerCase()
                );
    }
}