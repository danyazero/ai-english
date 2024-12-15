package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zero.aienglish.entity.UserVocabulary;
import org.zero.aienglish.entity.VocabularyHistory;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.VocabularyWordDTO;
import org.zero.aienglish.repository.*;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecomendationService {
    private final VocabularyHistoryRepository vocabularyHistoryRepository;
    private final UserVocabularyRepository userVocabularyRepository;
    private final VocabularyRepository vocabularyRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;

    public Optional<VocabularyWordDTO> nextWord(Integer userId) {
        markPreviousByStatusId(userId, 3);

        return getRecommendedWord(userId);
    }

    @Transactional
    public UserVocabulary saveWord(Integer userId, Integer wordId) {
        setUserReactionStatus(userId, wordId, 2);

        var userReference = userRepository.getReferenceById(userId);
        var word = vocabularyRepository.findById(wordId).orElse(null);

        var userVocabulary = UserVocabulary.builder()
                .user(userReference)
                .word(word)
                .build();

        return userVocabularyRepository.save(userVocabulary);
    }

    private void setUserReactionStatus(Integer userId, Integer wordId, Integer statusId) {
        var logRow = vocabularyHistoryRepository.findFirstByUser_IdAndVocabulary_Id(userId, wordId);
        if (logRow.isEmpty()) {
            log.warn("Log row not founded for user -> {}, and word -> {}", userId, wordId);
            throw new RequestException("Log row not founded");
        }

        var statusReference = statusRepository.getReferenceById(statusId);
        logRow.get().setStatus(statusReference);
        vocabularyHistoryRepository.save(logRow.get());
    }

    public void removeWord(Integer userId, Integer wordId) {
        setUserReactionStatus(userId, wordId, 3);
        userVocabularyRepository.deleteByWord_IdAndUser_Id(wordId, userId);
    }

    private Optional<VocabularyWordDTO> getRecommendedWord(Integer userId) {
        var recommendedWord = vocabularyRepository.getVocabulary(userId);
        if (recommendedWord.isEmpty()) {
            return Optional.empty();
        }

        var userReference = userRepository.getReferenceById(userId);
        var vocabularyReference = vocabularyRepository.getReferenceById(recommendedWord.get().getId());
        var askedStatusReference = statusRepository.getReferenceById(1);

        var vocabularyHistory = VocabularyHistory.builder()
                .at(Instant.now())
                .user(userReference)
                .vocabulary(vocabularyReference)
                .status(askedStatusReference)
                .build();
        vocabularyHistoryRepository.save(vocabularyHistory);

        return recommendedWord;
    }

    private void markPreviousByStatusId(Integer userId, Integer statustId) {
        var lastAsked = vocabularyHistoryRepository.findFirstByStatus_IdAndUser_Id(1, userId);
        if (lastAsked.isPresent()) {
            var ignoredStatus = statusRepository.getReferenceById(statustId);
            lastAsked.get().setStatus(ignoredStatus);
            vocabularyHistoryRepository.save(lastAsked.get());
        }
    }


}
