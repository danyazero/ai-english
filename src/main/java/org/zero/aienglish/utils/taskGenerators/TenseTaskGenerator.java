package org.zero.aienglish.utils.taskGenerators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.SentenceUserHistory;
import org.zero.aienglish.entity.Tense;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.mapper.TenseMapper;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.*;
import org.zero.aienglish.service.SentenceService;
import org.zero.aienglish.utils.AccuracyCheck;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenseTaskGenerator implements TaskGenerator {
    private final TenseMapper tenseMapper;
    private final AccuracyCheck accuracyCheck;
    private final UserRepository userRepository;
    private final TenseRepository tenseRepository;
    private final SentenceService sentenceService;
    private final SentenceRepository sentenceRepository;
    private final SentenceTenseRepository sentenceTenseRepository;
    private final AnswersHistoryRepository answersHistoryRepository;

    @Override
    public TaskType getTaskName() {
        return TaskType.tenseMarkers;
    }

    @Override
    public SentenceTask generateTask(SentenceDTO selectedSentence) {
        var sentenceDetails = sentenceService.getSentenceDetails(selectedSentence);
        var tenseTitle = getTenseTitle(sentenceDetails.tenses());
        log.info("tenseTitle -> {}", tenseTitle);

        var randomTenses = tenseRepository.getRandomTenseList(sentenceDetails.id());
        log.info("randomTenses -> {}", randomTenses.stream().map(org.zero.aienglish.model.Tense::getTense).toList());

        var mappedTenses = randomTenses.stream()
                .map(tenseMapper::map)
                .collect(Collectors.toList());
        log.info("mappedTenses ({}) -> {}", mappedTenses.size(), mappedTenses.stream().map(TenseDTO::tense).toList());

        var correctTense = TenseDTO.builder()
                .tense(tenseTitle)
                .build();
        log.info("correctTense -> {}", correctTense);
        mappedTenses.add(correctTense);
        log.info("mappedTenses ({}) -> {}", mappedTenses.size(), mappedTenses.stream().map(TenseDTO::tense).toList());

        var answers = mappedTenses.stream()
                .map(tenseMapper::map)
                .collect(Collectors.toList());
        Collections.shuffle(answers);
        log.info("answers ({}) -> {}", answers.size(), answers);

        return SentenceTask.builder()
                .title(sentenceDetails.sentence())
                .sentenceId(sentenceDetails.id())
                .taskType(TaskType.tenseMarkers)
                .words(sentenceDetails.words())
                .answers(answers)
                .tenses(null)
                .build();
    }

    @Override
    public CheckResult checkTask(Integer userId, TaskResultDTO result) {
        log.info("Task completion result -> {}", result);
        var sentence = sentenceRepository.findById(result.taskId());
        if (sentence.isEmpty()) {
            log.warn("Sentence for check not found");
            throw new RequestException("Sentence for check not found");
        }
        var tenseList = sentenceTenseRepository.findAllBySentence(sentence.get());
        var convertedTenseList = getTenseTitle(tenseList);
        var resultAccuracy = accuracyCheck.apply(result.wordList().getFirst().getWord(), convertedTenseList);

        var user = userRepository.getReferenceById(userId);
        var answerHistory = SentenceUserHistory.builder()
                .user(user)
                .sentence(sentence.get())
                .lastAnswered(Instant.now())
                .accuracy(resultAccuracy.doubleValue())
                .build();
        answersHistoryRepository.save(answerHistory);


        return CheckResult.builder()
                .mark(resultAccuracy)
                .accepted(resultAccuracy > 98F)
                .build();
    }

    private static String getTenseTitle(List<Tense> tense) {
        var correctTenseTitle = tense.stream()
                .map(mapTenseDTO())
                .toArray(String[]::new);
        return String.join(", ", correctTenseTitle);
    }

    private static Function<Tense, String> mapTenseDTO() {
        return tense ->  tense.getTitleTime().getTitle() + " " + tense.getTitleDuration().getTitle();
    }
}
