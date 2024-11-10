package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.zero.aienglish.entity.*;
import org.zero.aienglish.exception.DataIncorrectException;
import org.zero.aienglish.exception.NotFoundException;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.mapper.SentenceMapper;
import org.zero.aienglish.model.Tense;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.*;
import org.zero.aienglish.utils.*;
import org.zero.aienglish.utils.SpeechPart;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SentenceService {
    private final Validator validator;
    private final SpeechPart speechPart;
    private final TaskManager taskManager;
    private final TitleCaseWord titleCaseWord;
    private final SentenceMapper sentenceMapper;
    private final ThemeRepository themeRepository;
    private final TenseRepository tenseRepository;
    private final SpeechRepository speechRepository;
    private final SentenceRepository sentenceRepository;
    private final VocabularyRepository vocabularyRepository;
    private final SentenceTenseRepository sentenceTenseRepository;
    private final VocabularySentenceRepository vocabularySentenceRepository;


    public SentenceTask getSentenceTask(Integer userId) {
        var selectedSentence = sentenceRepository.getSentenceForUser(userId);
        if (selectedSentence.isEmpty()) {
            log.warn("Sentence for task not found for user -> {}", userId);
            throw new RequestException("Sentence for task not found.");
        }

        if (isSentenceHasSuitableScore(selectedSentence)) {
            log.info("Selected words doesnt have score or it less then 90");

            return taskManager.generateTask(TaskType.omittedWord, selectedSentence.get());
        }

        log.info("Selected words is suitable. Selecting generator randomly.");
        return taskManager.generateTaskWithRandomGenerator(selectedSentence.get());
    }


    @Transactional
    public void addSentence(@Validated org.zero.aienglish.model.Sentence sentence, Integer themeId) {
        log.info("Add words -> {}", sentence.sentence());

        var validationResult = validator.validateObject(sentence);
        if (validationResult.hasErrors()) {
            log.warn("Provided words with incorrect data -> {}", validationResult.getAllErrors());
            throw new DataIncorrectException("Sentence provided with incorrect data");
        }

        log.info("Sentence: {}, word list: {}", sentence, sentence.vocabulary());
        var theme = getThemeIfExist(themeId);
        var tense = getTenseIfExist(sentence);
        isSentenceAlreadyExists(sentence);

        var mappedSentence = sentenceMapper.map(sentence);

        theme.ifPresentOrElse(
                mappedSentence::setTheme,
                () -> {
                    log.info("Theme is provided but not found");
                    throw new RequestException("Theme not found");
                });


        var vocabularyList = getVocabularyList(sentence.vocabulary());
        log.info("Getted vocabulary list size -> {}", vocabularyList.size());

        var sentenceSaved = sentenceRepository.save(mappedSentence);
        log.info("Saved words: {}", sentenceSaved.getSentence());

        var tenseReferanceList = getTenseReferenceList(tense, sentenceSaved);
        log.info("Sentence include {} tenses", tenseReferanceList.size());
        sentenceTenseRepository.saveAll(tenseReferanceList);

        var vocabularyListSaved = vocabularyRepository.saveAll(vocabularyList);
        log.info("Saved vocabulary list: {}", vocabularyListSaved.stream().map(VocabularyEntity::getWord).toList());

        var vocabularySentenceList = getVocabularySentenceList(vocabularyListSaved, sentenceSaved, sentence.vocabulary());
        vocabularySentenceRepository.saveAll(vocabularySentenceList);
    }

    public List<VocabularyEntity> getVocabularyList(List<WordDTO> wordList) {
        var speechPartArray = getSpeechPartArray(wordList);
        log.info("Speech part array retrieved");

        var speechPartList = speechRepository.findByTitle(speechPartArray);
        log.info("Retrieved speech part list from database");

        if (speechPartList.isEmpty()) {
            log.info("Any of speech part not found");
            throw new RequestException("Any of speech part not found");
        }

        var wordArray = getWordArray(wordList);
        var alreadyExistedWords = vocabularyRepository.findAllByWord(wordArray);

        return wordList.stream()
                .map(element -> {
                    Optional<VocabularyEntity> vocabulary = wordExistCheck(alreadyExistedWords, element);
                    return vocabulary.orElseGet(() -> speechPart.apply(element, speechPartList));
                })
                .filter(Objects::nonNull)
                .map(titleCaseWord)
                .toList();
    }

    private static boolean isSentenceHasSuitableScore(Optional<SentenceDTO> selectedSentence) {
        return selectedSentence.get().getScore().isNaN() || selectedSentence.get().getScore() < 90.0;
    }

    public CheckResult checkTask(Integer userId, TaskResultDTO taskResult) {
        return taskManager.checkResult(userId, taskResult);
    }

    private Optional<VocabularyEntity> wordExistCheck(
            List<VocabularyEntity> alreadyExistedWords,
            WordDTO currentWord
    ) {
            return alreadyExistedWords.stream()
                    .filter(element ->
                            element.getWord().equalsIgnoreCase(currentWord.getDefaultWord())
                                    && element.getSpeechPart().getTitle().equalsIgnoreCase(currentWord.getSpeechPart())).findFirst();

    }

    private void isSentenceAlreadyExists(Sentence sentence) {
        sentenceRepository.findFirstBySentenceLikeIgnoreCase(sentence.sentence())
                .ifPresent(s -> { throw new RequestException("Sentence already exists"); });
    }

    private List<Tense> getTenseIfExist(Sentence sentence) {
        return Optional.ofNullable(sentence.sentenceTense())
                .map(tenseRepository::getTenseListByTitle).orElseThrow(() -> new NotFoundException("Tense not found"));
    }

    private List<SentenceTenseEntity> getTenseReferenceList(List<Tense> tense, SentenceEntity sentenceSaved) {
        return tense.stream()
                .map(t -> tenseRepository.getReferenceById(t.getId()))
                .map(t -> new SentenceTenseEntity(t, sentenceSaved))
                .toList();
    }

    private static String[] getSpeechPartArray(List<WordDTO> wordList) {
        return wordList.stream()
                .map(WordDTO::getSpeechPart)
                .toArray(String[]::new);
    }

    private static String[] getWordArray(List<WordDTO> wordList) {
        return wordList.stream().map(WordDTO::getDefaultWord).toArray(String[]::new);
    }

    private Optional<ThemeEntity> getThemeIfExist(Integer themeId) {
        return Optional.of(themeId)
                .map(themeRepository::findById).orElseThrow(() -> new RequestException("Theme not found"));
    }

    private static List<VocabularySentenceEntity> getVocabularySentenceList(
            List<VocabularyEntity> vocabularyListSaved,
            SentenceEntity sentenceSaved,
            List<WordDTO> vocabulary) {
        log.info("Casting into vocabulary list");
        return vocabularyListSaved.stream()
                .map(element -> {
                    var foundedWord = getWordByTitle(vocabulary, element);
                    if (foundedWord.isEmpty()) {
                        log.warn("For current word {} not found", element.getWord());
                        return new VocabularySentenceEntity(sentenceSaved, element);
                    }
                    vocabulary.remove(foundedWord.get());
                    log.info("For current word {} founded -> {}, {}", element.getWord(), foundedWord.get().getWord(), foundedWord.get().getOrder());

                    return VocabularySentenceEntity.builder()
                            .sentence(sentenceSaved)
                            .vocabulary(element)
                            .order(foundedWord.get().getOrder())
                            .defaultWord(foundedWord.get().getWord())
                            .isMarker(foundedWord.get().getIsMarker())
                            .build();

                })
                .toList();
    }

    private static Optional<WordDTO> getWordByTitle(List<WordDTO> vocabulary, VocabularyEntity element) {
        return vocabulary.stream().filter(word -> element.getWord().equalsIgnoreCase(word.getWord())).findFirst();
    }
}
