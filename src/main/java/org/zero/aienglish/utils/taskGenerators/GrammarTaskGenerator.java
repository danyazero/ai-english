package org.zero.aienglish.utils.taskGenerators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.aienglish.entity.Sentence;
import org.zero.aienglish.entity.VocabularySentence;
import org.zero.aienglish.lib.grpc.Task;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.SentenceHistoryRepository;
import org.zero.aienglish.repository.StatusRepository;
import org.zero.aienglish.repository.VocabularySentenceRepository;
import org.zero.aienglish.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrammarTaskGenerator extends TaskGenerator {
    private final VocabularySentenceRepository vocabularySentenceRepository;
    private final SentenceHistoryRepository sentenceHistoryRepository;
    private final ContinuousFormConverter ContinuousFormConverter;
    private final SimpleFormConverter simpleFormConverter;
    private final SentenceWordOmit sentenceWordOmit;
    private final StatusRepository statusRepository;
    private final DistinctWords distinctWords;
    private final AccuracyCheck accuracyCheck;

    @Override
    public TaskType getTaskName() {
        return TaskType.GRAMMAR;
    }

    @Override
    public SentenceTask generateTask(SentenceDTO selectedSentence) {
        var correctAnswers = getCorrectAnswers(selectedSentence);
        var formattedSentence = sentenceWordOmit.apply(selectedSentence.getSentence(), correctAnswers);

        var randomAnswers = vocabularySentenceRepository.findRandomGrammarWords(selectedSentence.getId(), 3);
        var randomAnswersSentences = getRandomAnswersSentences(randomAnswers);

        log.info("Random sentences -> {}", randomAnswersSentences);

        var formattedAnswers = new ArrayList<WordResponseDTO>();
        for (Integer sentence : randomAnswersSentences) {

            var words = randomAnswers.stream()
                    .filter(element -> Objects.equals(element.getId(), sentence))
                    .toList();

            var stepsAmount = Math.min(correctAnswers.size(), words.size());

            for (var i = 0; i < stepsAmount; i++) {
                var currentWord = words.get(i);
                var correctAnswer = Optional.ofNullable(correctAnswers.get(i));

                if (isNotModal(correctAnswer, currentWord)) break;

                var isNegative = isNegative(correctAnswer);

                var answerDTO = AnswerDTO.builder()
                        .sentenceId(sentence)
                        .correct(correctAnswer)
                        .isNegative(isNegative)
                        .word(currentWord)
                        .order(i)
                        .build();

                addDefaultAnswer(answerDTO, formattedAnswers);

                if (isSuitableForModification(correctAnswer, currentWord)) {
                    addContinuousAnswer(answerDTO, formattedAnswers);

                    var answerFirstForm = getFormattedGeneratedAnswer(answerDTO, GrammarDTO::getFirstForm);
                    formattedAnswers.add(answerFirstForm);

                    if (!isVerb(currentWord)) {
                        var currentAnswerSecondForm = getFormattedGeneratedAnswer(answerDTO, GrammarDTO::getSecondForm);
                        formattedAnswers.add(currentAnswerSecondForm);

                        addThirdFormAnswer(answerDTO, formattedAnswers);
                        addSimpleFormAnswer(answerDTO, formattedAnswers);
                    }
                }

            }
        }

        var distinctAnswers = deduplicateAnswers(formattedAnswers, correctAnswers);

        return SentenceTask.builder()
                .caption(selectedSentence.getTranslate())
                .sentenceId(selectedSentence.getId())
                .theme(selectedSentence.getTheme())
                .steps(correctAnswers.size())
                .taskType(TaskType.GRAMMAR)
                .pattern(formattedSentence)
                .answers(distinctAnswers)
                .title(formattedSentence)
                .build();
    }

    private void addSimpleFormAnswer(AnswerDTO answerDTO, ArrayList<WordResponseDTO> formattedAnswers) {
        if (isCouldBeUsedSimpleForm(answerDTO.word(), answerDTO.isNegative())) {
            Function<GrammarDTO, String> extractor = word -> simpleFormConverter.apply(word.getFirstForm());
            var currentAnswerSimpleForm = getGeneratedAnswer(answerDTO, extractor);
            formattedAnswers.add(currentAnswerSimpleForm);
        }
    }

    private static void addThirdFormAnswer(AnswerDTO answerDTO, ArrayList<WordResponseDTO> formattedAnswers) {
        if (!answerDTO.isNegative()) {
            var currentAnswerThirdForm = getGeneratedAnswer(answerDTO, GrammarDTO::getThirdForm);
            formattedAnswers.add(currentAnswerThirdForm);
        }
    }

    private static void addDefaultAnswer(AnswerDTO answerDTO, List<WordResponseDTO> formattedAnswers) {
        var answer = getFormattedGeneratedAnswer(answerDTO, GrammarDTO::getDefaultWord);

        formattedAnswers.add(answer);
    }

    private void addContinuousAnswer(AnswerDTO answerDTO, List<WordResponseDTO> formattedAnswers) {
        if (!answerDTO.isNegative() && !answerDTO.word().getIsModal()) {
            Function<GrammarDTO, String> extractor = word -> ContinuousFormConverter.apply(word.getFirstForm());
            var currentAnswerContForm = getGeneratedAnswer(answerDTO, extractor);

            formattedAnswers.add(currentAnswerContForm);
        }
    }

    private static WordResponseDTO getFormattedGeneratedAnswer(AnswerDTO answerDTO, Function<GrammarDTO, String> extractor) {
        var currentFormattedWord = getFormattedWord(
                extractor.apply(answerDTO.word()),
                answerDTO.isNegative()
        );

        return getGeneratedAnswer(answerDTO, currentFormattedWord);
    }

    private static WordResponseDTO getGeneratedAnswer(AnswerDTO answerDTO, Function<GrammarDTO, String> extractor) {
        return getGeneratedAnswer(answerDTO, extractor.apply(answerDTO.word()));
    }

    private static WordResponseDTO getGeneratedAnswer(AnswerDTO answerDTO, String currentFormattedWord) {
        return WordResponseDTO.builder()
                .word(currentFormattedWord)
                .id(answerDTO.sentenceId())
                .order(answerDTO.order())
                .translate("__")
                .build();
    }

    private List<WordResponseDTO> deduplicateAnswers(List<WordResponseDTO> formattedAnswers, List<TaskWord> correctAnswerWords) {
        return IntStream.range(0, correctAnswerWords.size())
                .mapToObj(order -> distinctWords.apply(formattedAnswers.stream()
                        .filter(answer -> answer.getOrder() == order)
                        .toList()))
                .flatMap(List::stream)
                .toList();
    }

    private static boolean isCouldBeUsedSimpleForm(GrammarDTO currentWord, boolean isNegative) {
        return currentWord.getSecondForm().equalsIgnoreCase(currentWord.getThirdForm()) && !isNegative;
    }

    private static boolean isNotModal(Optional<TaskWord> correctAnswer, GrammarDTO currentWord) {
        return correctAnswer.map(TaskWord::isModal).orElse(false) != currentWord.getIsModal();
    }

    private static List<Integer> getRandomAnswersSentences(List<GrammarDTO> randomAnswers) {
        return randomAnswers.stream()
                .map(GrammarDTO::getId)
                .distinct()
                .toList();
    }

    private List<TaskWord> getCorrectAnswers(SentenceDTO selectedSentence) {
        return vocabularySentenceRepository.findAllBySentence_IdAndIsMarkerOrderByOrder(
                        selectedSentence.getId(),
                        true
                )
                .stream()
                .map(GrammarTaskGenerator::mapToWordResponseDTO)
                .toList();
    }

    private static boolean isNegative(Optional<TaskWord> correctAnswer) {
        return correctAnswer.filter(taskWord -> taskWord.word().endsWith("n't")).isPresent();
    }

    private static String getFormattedWord(String word, boolean isNegative) {
        return isNegative ? !word.contains("n't") ? word + "n't" : word : word.contains("n't") ? word.replaceAll("n't", "") : word;
    }

    private static boolean isVerb(GrammarDTO currentWord) {
        return Objects.equals(currentWord.getSecondForm(), "--");
    }

    private static boolean isSuitableForModification(Optional<TaskWord> correctAnswer, GrammarDTO currentWord) {
        return isCorrectAnswer(correctAnswer, currentWord) && currentWord.getFirstForm() != null;
    }

    private static boolean isCorrectAnswer(Optional<TaskWord> word, GrammarDTO currentAnswer) {
        return word.filter(taskWord -> Objects.equals(taskWord.word(), currentAnswer.getDefaultWord())).isPresent();
    }

    private static TaskWord mapToWordResponseDTO(VocabularySentence word) {
        return TaskWord.builder()
                .defaultWord(word.getVocabulary() == null ? "" : word.getVocabulary().getWord())
                .order(Integer.valueOf(word.getOrder()))
                .word(word.getDefaultWord())
                .isModal(word.getIsModal())
                .build();
    }

    @Override
    public TaskCheckResult checkTask(Integer userId, String result, Sentence sentence) {
        result = result.replaceAll(" \\((\\w+)\\)", "");
        var correctAnswer = sentence.getSentence().replaceAll("[*]", "");
        log.info("Formatted user answer -> {}, formatted correct answer -> {}", result, correctAnswer);
        var checkResult = accuracyCheck.apply(correctAnswer, result);
        this.saveTaskLog(checkResult > 98F, userId, statusRepository, sentenceHistoryRepository);

        return TaskCheckResult.builder()
                .correctAnswer(correctAnswer)
                .accepted(checkResult > 99F)
                .userAnswer(result)
                .mark(checkResult)
                .build();
    }
}
