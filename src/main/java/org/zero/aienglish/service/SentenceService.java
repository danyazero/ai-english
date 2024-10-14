package org.zero.aienglish.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.zero.aienglish.entity.SpeechPart;
import org.zero.aienglish.entity.Vocabulary;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.mapper.WordMapper;
import org.zero.aienglish.model.SentenceDTO;
import org.zero.aienglish.model.WordDTO;
import org.zero.aienglish.repository.SentenceRepository;
import org.zero.aienglish.repository.SpeechRepository;
import org.zero.aienglish.repository.VocabularyRepository;
import org.zero.aienglish.repository.VocabularySentenceRepository;
import org.zero.aienglish.utils.GetVocabularyWithSpeechPart;
import org.zero.aienglish.utils.SetSpeechPart;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SentenceService {
    private final WordMapper wordMapper;
    private final SetSpeechPart setSpeechPart;
    private final SpeechRepository speechRepository;
    private final SentenceRepository sentenceRepository;
    private final VocabularyRepository vocabularyRepository;
    private final GetVocabularyWithSpeechPart getVocabularyWithSpeechPart;
    private final VocabularySentenceRepository vocabularySentenceRepository;


    @Transactional
    public void addSentence(SentenceDTO sentence, WordDTO word) {

    }

    public List<Vocabulary> getWordList(List<WordDTO> wordList) {
        var speechPartList = wordList.stream().map(WordDTO::speechPart).toList();
        log.info("Speech part list: {}", speechPartList);
        var speechPart = speechRepository.findByTitle(speechPartList.toArray(new String[0]));
        log.info("Retrieved speech part from database {}", speechPart.stream().map(SpeechPart::getTitle).toList());
        if (speechPart.isEmpty()) {
            log.info("Any of speech part not found");
            throw new RequestException("Any of speech part not found");
        }

        return wordList.stream()
                .map(element -> getVocabularyWithSpeechPart.apply(element, speechPart))
                .filter(Objects::nonNull)
                .toList();
    }
}
