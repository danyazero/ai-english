package org.zero.aienglish.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.zero.aienglish.entity.*;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.mapper.SentenceMapper;
import org.zero.aienglish.model.SentenceDTO;
import org.zero.aienglish.model.WordDTO;
import org.zero.aienglish.repository.*;
import org.zero.aienglish.utils.NotExist;
import org.zero.aienglish.utils.TitleCaseWord;
import org.zero.aienglish.utils.WithSpeechPart;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SentenceService {
    private final NotExist notExist;
    private final TitleCaseWord titleCaseWord;
    private final WithSpeechPart withSpeechPart;
    private final SentenceMapper sentenceMapper;
    private final ThemeRepository themeRepository;
    private final SpeechRepository speechRepository;
    private final SentenceRepository sentenceRepository;
    private final VocabularyRepository vocabularyRepository;
    private final VocabularySentenceRepository vocabularySentenceRepository;


    @Transactional
    public void addSentence(SentenceDTO sentence, List<WordDTO> wordList) {
        log.info("Sentence: {}, word list: {}", sentence, wordList);
        var theme = getThemeIdExist(sentence);

        Optional.ofNullable(sentence.sentence()).map(sentenceRepository::findFirstBySentenceLikeIgnoreCase)
                .ifPresent(s -> {
                    throw new RequestException("Sentence already exists");
                });

        var mappedSentence = sentenceMapper.map(sentence);

        theme.ifPresentOrElse(
                mappedSentence::setTheme,
                () -> {
                    log.info("Theme is provided but not found");
                    throw new RequestException("Theme not found");
                });

        var vocabularyList = getVocabularyList(wordList);

        var sentenceSaved = sentenceRepository.save(mappedSentence);
        log.info("Saved sentence: {}", sentenceSaved.getSentence());

        var vocabularyListSaved = vocabularyRepository.saveAll(vocabularyList);
        log.info("Saved vocabulary list: {}", vocabularyListSaved.stream().map(Vocabulary::getWord).toList());

        var vocabularySentenceList = getVocabularySentenceList(vocabularyListSaved, sentenceSaved);
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
                .filter(word -> notExist.apply(alreadyExistedWords, word))
                .map(element -> withSpeechPart.apply(element, speechPartList))
                .filter(Objects::nonNull)
                .map(titleCaseWord)
                .toList();
    }

    private static String[] getSpeechPartArray(List<WordDTO> wordList) {
        return wordList.stream()
                .map(WordDTO::getSpeechPart)
                .toArray(String[]::new);
    }

    private static String[] getWordArray(List<WordDTO> wordList) {
        return wordList.stream().map(WordDTO::getWord).toArray(String[]::new);
    }

    private Optional<Theme> getThemeIdExist(SentenceDTO sentence) {
        return Optional.ofNullable(sentence.theme())
                .map(themeRepository::findById).orElseThrow(() -> new RequestException("Theme not found"));
    }

    private static List<VocabularySentence> getVocabularySentenceList(
            List<Vocabulary> vocabularyListSaved,
            Sentence sentenceSaved
    ) {
        return vocabularyListSaved.stream()
                .map(element -> new VocabularySentence(sentenceSaved, element))
                .toList();
    }
}
