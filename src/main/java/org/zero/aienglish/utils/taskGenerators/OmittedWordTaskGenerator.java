package org.zero.aienglish.utils.taskGenerators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.mapper.TenseMapper;
import org.zero.aienglish.mapper.WordMapper;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.VocabularySentenceRepository;
import org.zero.aienglish.utils.Random;
import org.zero.aienglish.utils.SentenceCheck;
import org.zero.aienglish.utils.SentenceDetailsExtractor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OmittedWordTaskGenerator implements TaskGenerator {
    private static final String[] speechPartIgnoreList = new String[]{"article", "preposition", "pronoun"};
    private final SentenceDetailsExtractor sentenceDetailsExtractor;
    private final VocabularySentenceRepository vocabularyRepository;
    private final SentenceCheck sentenceCheck;
    private final TenseMapper tenseMapper;
    private final WordMapper wordMapper;

    @Override
    public TaskType getTaskName() {
        return TaskType.omittedWord;
    }

    @Override
    @Transactional
    public SentenceTask generateTask(SentenceDTO selectedSentence) {
        var sentenceTask = sentenceDetailsExtractor.apply(selectedSentence);

        var omitted = getWordListWithOmittedWord(sentenceTask.words());

        log.info(
                "For words with id -> {}, successfully omitted word, list with size -> {}",
                sentenceTask.id(), omitted.wordList().size()
        );

        var answerWordList = vocabularyRepository.getRandomWordList(
                        3,
                        omitted.omittedWord().getWord(),
                        speechPartIgnoreList
                ).stream()
                .map(wordMapper::map).collect(Collectors.toList());
        answerWordList.add(omitted.omittedWord());

        log.info("Generated answer word list with size -> {}", answerWordList.size());

        List<TenseDTO> mappedTenses = sentenceTask.tenses().stream().map(tenseMapper::map).toList();

        return SentenceTask.builder()
                .taskType(this.getTaskName())
                .sentenceId(sentenceTask.id())
                .title(sentenceTask.translate())
                .tenses(mappedTenses)
                .words(omitted.wordList())
                .answers(answerWordList)
                .build();
    }

    @Override
    public CheckResult checkTask(Integer userId, TaskResultDTO result) {
        return sentenceCheck.apply(userId, result);
    }

    private static OmittedWord getWordListWithOmittedWord(List<WordResponseDTO> vocabulary) {
        var relevantWordList = vocabulary.stream()
                .filter(OmittedWordTaskGenerator::isWordRelevant)
                .toList();
        log.info("Founded {} relevant words", relevantWordList.size());
        var randomWordIndex = Random.nextInRange(0, relevantWordList.size() - 1);
        log.info("Generated next random int -> {}", randomWordIndex);
        var omittedWordId = relevantWordList.get(randomWordIndex).getId();
        log.info("Omitted word id -> {}", omittedWordId);


        var omittedWord = vocabulary.stream()
                .filter(word -> word.getId().equals(omittedWordId)).findFirst().orElseThrow(() -> new RequestException("Omitted word not found."));
        log.info("Omitted word -> {}", omittedWord.getWord());

        List<WordResponseDTO> wordList = vocabulary.stream()
                .map(word -> getWordOrReplaceWithEmpty(word, omittedWordId))
                .toList();
        log.info("Omitted word list -> {}", wordList.size());
        return new OmittedWord(wordList, omittedWord);
    }

    protected record OmittedWord(
            List<WordResponseDTO> wordList,
            WordResponseDTO omittedWord
    ) {
    }

    private static WordResponseDTO getWordOrReplaceWithEmpty(WordResponseDTO word, Integer omittedWordId) {
        if (Objects.equals(word.getId(), omittedWordId)) return new WordResponseDTO();
        return word;
    }

    private static boolean isWordRelevant(WordResponseDTO word) {
        return !Arrays.stream(speechPartIgnoreList).toList().contains(word.getSpeechPart().getTitle().toLowerCase());
    }
}
