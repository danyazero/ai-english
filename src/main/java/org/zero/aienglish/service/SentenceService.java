package org.zero.aienglish.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.zero.aienglish.entity.*;
import org.zero.aienglish.exception.DataIncorrectException;
import org.zero.aienglish.exception.NotFoundException;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.mapper.SentenceMapper;
import org.zero.aienglish.model.SentenceDTO;
import org.zero.aienglish.model.TenseDTO;
import org.zero.aienglish.model.WordDTO;
import org.zero.aienglish.repository.*;
import org.zero.aienglish.utils.TitleCaseWord;
import org.zero.aienglish.utils.SpeechPart;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SentenceService {
    private final Validator validator;
    private final TitleCaseWord titleCaseWord;
    private final SpeechPart speechPart;
    private final SentenceMapper sentenceMapper;
    private final ThemeRepository themeRepository;
    private final TenseRepository tenseRepository;
    private final SpeechRepository speechRepository;
    private final SentenceRepository sentenceRepository;
    private final VocabularyRepository vocabularyRepository;
    private final SentenceTenseRepository sentenceTenseRepository;
    private final VocabularySentenceRepository vocabularySentenceRepository;


    @Transactional
    public void addSentence(@Validated SentenceDTO sentence, Integer themeId) {
        log.info("Add sentence -> {}", sentence.sentence());

        var validationResult = validator.validateObject(sentence);
        if (validationResult.hasErrors()) {
            log.warn("Provided sentence with incorrect data -> {}", validationResult.getAllErrors());
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
        log.info("Saved sentence: {}", sentenceSaved.getSentence());

        var tenseReferanceList = getTenseReferenceList(tense, sentenceSaved);
        log.info("Sentence include {} tenses", tenseReferanceList.size());
        sentenceTenseRepository.saveAll(tenseReferanceList);

        var vocabularyListSaved = vocabularyRepository.saveAll(vocabularyList);
        log.info("Saved vocabulary list: {}", vocabularyListSaved.stream().map(Vocabulary::getWord).toList());

        var vocabularySentenceList = getVocabularySentenceList(vocabularyListSaved, sentenceSaved, sentence.vocabulary());
        vocabularySentenceRepository.saveAll(vocabularySentenceList);
    }

    public List<Vocabulary> getVocabularyList(List<WordDTO> wordList) {
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
                .filter(word -> wordExistCheck(alreadyExistedWords, word))
                .map(element -> speechPart.apply(element, speechPartList))
                .filter(Objects::nonNull)
                .map(titleCaseWord)
                .toList();
    }

    private Boolean wordExistCheck(
            List<Vocabulary> alreadyExistedWords,
            WordDTO currentWord
    ) {
            return alreadyExistedWords.stream()
                    .noneMatch(element ->
                            element.getWord().equalsIgnoreCase(currentWord.getDefaultWord())
                                    && element.getSpeechPart().getTitle().equalsIgnoreCase(currentWord.getSpeechPart()));

    }

    private void isSentenceAlreadyExists(SentenceDTO sentence) {
        sentenceRepository.findFirstBySentenceLikeIgnoreCase(sentence.sentence())
                .ifPresent(s -> { throw new RequestException("Sentence already exists"); });
    }

    private List<TenseDTO> getTenseIfExist(SentenceDTO sentence) {
        return Optional.ofNullable(sentence.sentenceTense())
                .map(tenseRepository::getTenseListByTitle).orElseThrow(() -> new NotFoundException("Tense not found"));
    }

    private List<SentenceTense> getTenseReferenceList(List<TenseDTO> tense, Sentence sentenceSaved) {
        return tense.stream()
                .map(t -> tenseRepository.getReferenceById(t.getId()))
                .map(t -> new SentenceTense(t, sentenceSaved))
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

    private Optional<Theme> getThemeIfExist(Integer themeId) {
        return Optional.of(themeId)
                .map(themeRepository::findById).orElseThrow(() -> new RequestException("Theme not found"));
    }

    private static List<VocabularySentence> getVocabularySentenceList(
            List<Vocabulary> vocabularyListSaved,
            Sentence sentenceSaved,
            List<WordDTO> vocabulary) {
        log.info("Casting into vocabulary list");
        return vocabularyListSaved.stream()
                .map(element -> {
                    var foundedWord = getWordByTitle(vocabulary, element);
                    if (foundedWord.isEmpty()) {
                        log.warn("For current word {} not found", element.getWord());
                        return new VocabularySentence(sentenceSaved, element);
                    }
                    log.info("For current word {} founded -> {}, {}", element.getWord(), foundedWord.get().getWord(), foundedWord.get().getOrder());

                    return new VocabularySentence(
                            sentenceSaved,
                            element,
                            foundedWord.get().getOrder(),
                            foundedWord.get().getDefaultWord(),
                            foundedWord.get().getIsMarker()
                    );

                })
                .toList();
    }

    private static Optional<WordDTO> getWordByTitle(List<WordDTO> vocabulary, Vocabulary element) {
        return vocabulary.stream().filter(word -> element.getWord().equalsIgnoreCase(word.getWord())).findFirst();
    }
}
