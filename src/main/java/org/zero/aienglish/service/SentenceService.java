package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zero.aienglish.entity.*;
import org.zero.aienglish.entity.Sentence;
import org.zero.aienglish.entity.Theme;
import org.zero.aienglish.entity.VocabularySentence;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.mapper.SentenceMapper;
import org.zero.aienglish.model.*;
import org.zero.aienglish.repository.*;
import org.zero.aienglish.utils.TitleCaseWord;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SentenceService {
    private final TitleCaseWord titleCaseWord;
    private final SentenceMapper sentenceMapper;
    private final ThemeRepository themeRepository;
    private final SentenceRepository sentenceRepository;
    private final VocabularyRepository vocabularyRepository;
    private final VocabularySentenceRepository vocabularySentenceRepository;

    @Transactional
    public void addSentence(org.zero.aienglish.model.Sentence sentence) {
        log.info("Add words -> {}", sentence.sentence());

        log.info("Sentence: {}, word list: {}", sentence, sentence.vocabulary());
        isSentenceAlreadyExists(sentence);

        var theme = getThemeIfExist(sentence.sentenceTheme());
        var mappedSentence = sentenceMapper.map(sentence);
        mappedSentence.setTheme(theme);

        var sentenceSaved = sentenceRepository.save(mappedSentence);
        log.info("Saved words: {}", sentenceSaved.getSentence());


        var vocabularySentenceList = getVocabularySentenceList(sentenceSaved, sentence.vocabulary());
        vocabularySentenceRepository.saveAll(vocabularySentenceList);
    }

    private List<VocabularySentence> getVocabularySentenceList(
            Sentence sentenceSaved,
            List<WordDTO> vocabulary) {
        log.info("Casting into vocabulary list");

        var vocabularyList = new ArrayList<VocabularySentence>();
        var wordArray = getWordArray(vocabulary);
        var alreadyExistedWords = vocabularyRepository.findAllByWord(wordArray);
        for (WordDTO wordDTO : vocabulary) {
            var vocabularySentence = VocabularySentence.builder()
                    .defaultWord(wordDTO.getDefaultWord())
                    .isModal(wordDTO.getIsModal() != null && wordDTO.getIsModal())
                    .isMarker(wordDTO.getIsMarker())
                    .order(wordDTO.getOrder())
                    .sentence(sentenceSaved);

            if (wordDTO.getWord() != null && !wordDTO.getWord().isEmpty()) {
                var isExist = alreadyExistedWords.stream()
                        .filter(element -> element.getWord().equalsIgnoreCase(wordDTO.getWord()))
                        .findFirst();
                if (isExist.isPresent()) {
                    vocabularySentence.vocabulary(isExist.get());
                } else {
                    log.info("Creating vocabulary for word -> {}", wordDTO);

                    var newVocabulary = Vocabulary.builder()
                            .word(wordDTO.getWord())
                            .secondForm(wordDTO.getSecondForm() == null ? "--" : wordDTO.getSecondForm())
                            .thirdForm(wordDTO.getThirdForm() == null ? "--" : wordDTO.getThirdForm())
                            .meaning(wordDTO.getMeaning() == null ? "--" : wordDTO.getMeaning())
                            .translate(wordDTO.getTranslate())
                            .build();

                    var capitalisedWord = titleCaseWord.apply(newVocabulary);
                    var savedWord = vocabularyRepository.save(capitalisedWord);
                    vocabularySentence.vocabulary(savedWord);
                }
            }

            var vocabularyElement = vocabularySentence.build();
            log.info("Vocabulary list element -> {}", vocabularyElement);
            vocabularyList.add(vocabularyElement);
        }
        log.info("Filled vocabulary list with {} elements", vocabularyList.size());

        return vocabularyList;
    }

    private Theme getThemeIfExist(String sentenceTense) {
        return themeRepository.findFirstByTitle(sentenceTense).orElseThrow(() -> new RequestException("Theme not found"));
    }

    private void isSentenceAlreadyExists(org.zero.aienglish.model.Sentence sentence) {
        sentenceRepository.findFirstBySentenceLikeIgnoreCase(sentence.sentence())
                .ifPresent(s -> { throw new RequestException("Sentence already exists"); });
    }

    private static String[] getWordArray(List<WordDTO> wordList) {
        return wordList.stream().map(WordDTO::getWord).toArray(String[]::new);
    }
}
