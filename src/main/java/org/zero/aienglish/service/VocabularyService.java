package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.zero.aienglish.entity.UserVocabulary;
import org.zero.aienglish.entity.VocabularyEntity;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.mapper.SentenceMapper;
import org.zero.aienglish.model.VocabularyWord;
import org.zero.aienglish.repository.*;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VocabularyService {
    private final VocabularySentenceRepository vocabularySentenceRepository;
    private final UserVocabularyRepository userVocabularyRepository;
    private final VocabularyRepository vocabularyRepository;
    private final SentenceRepository sentenceRepository;
    private final SentenceMapper sentenceMapper;
    private final UserRepository userRepository;

    public List<VocabularyEntity> getVocabulary(Integer userId, Integer page) {
        var pageObject = PageRequest.of(page, 10);

        return userVocabularyRepository.getAllByUser_Id(userId, pageObject).stream()
                .map(UserVocabulary::getWord)
                .toList();
    }

    public void saveWordToVocabulary(Integer wordId, Integer userId) {
        var sentenceWord = vocabularySentenceRepository.findById(wordId);
        if (sentenceWord.isEmpty()) {
            log.warn("Nothing found with this id.");
            throw new RequestException("Nothing found with this id.");
        }
        var userReference = userRepository.getReferenceById(userId);

        var newWord = UserVocabulary.builder()
                .user(userReference)
                .word(sentenceWord.get().getVocabulary())
                .lastSeen(Instant.now())
                .known(false)
                .build();

        userVocabularyRepository.save(newWord);
    }

    public VocabularyWord getWordForSentence(Integer wordId, Integer userId) {
        var vocabularyWord = vocabularySentenceRepository.findById(wordId);
        if (vocabularyWord.isEmpty()) {
            log.warn("Word with id -> {} not found", wordId);
            throw new RequestException("Word not found");
        }


        log.info("Word with sentenceWordId -> {} founded", wordId);

        return getVocabularyWord(vocabularyWord.get().getVocabulary(), userId);
    }

    public VocabularyWord getWordForVocabulary(Integer vocabularyId, Integer userId) {
        var vocabularyWord = vocabularyRepository.findById(vocabularyId);
        if (vocabularyWord.isEmpty()) {
            log.warn("Word with id -> {} not found", vocabularyId);
            throw new RequestException("Word not found");
        }

        return getVocabularyWord(vocabularyWord.get(), userId);
    }

    private VocabularyWord getVocabularyWord(VocabularyEntity word, Integer userId) {
        var sentenceList = sentenceRepository.getSentencesForWord(word.getId()).stream()
                .map(sentenceMapper::map)
                .toList();

        log.info("Sentences ({}) for word with id -> {} founded", sentenceList.size(), word.getId());

        var isAlreadySaved = userVocabularyRepository.findFirstByUser_IdAndWord_Id(userId, word.getId());

        return VocabularyWord.builder()
                .alreadySaved(isAlreadySaved.isPresent())
                .translation(word.getTranslate())
                .sentences(sentenceList)
                .word(word.getWord())
                .build();

    }
}
