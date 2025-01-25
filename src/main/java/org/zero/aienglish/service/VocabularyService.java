package org.zero.aienglish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zero.aienglish.entity.UserVocabulary;
import org.zero.aienglish.entity.VocabularySentence;
import org.zero.aienglish.exception.RequestException;
import org.zero.aienglish.model.Pagination;
import org.zero.aienglish.model.UserWordDTO;
import org.zero.aienglish.model.VocabularyWord;
import org.zero.aienglish.repository.UserRepository;
import org.zero.aienglish.repository.UserVocabularyRepository;
import org.zero.aienglish.repository.VocabularyRepository;
import org.zero.aienglish.repository.VocabularySentenceRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VocabularyService {
    private final VocabularySentenceRepository vocabularySentenceRepository;
    private final UserVocabularyRepository userVocabularyRepository;
    private final VocabularyRepository vocabularyRepository;
    private final UserRepository userRepository;

    public Pagination<UserWordDTO> getUserVocabulary(Integer userId, Integer page) {

        var vocabularies = vocabularyRepository.getUserVocabularyList(userId, 10, page);
        var totalPages = vocabularyRepository.getTotalPages(userId) / 10 + 1;


        return Pagination.<UserWordDTO>builder()
                .items(vocabularies)
                .totalPages(totalPages)
                .currentPage(page)
                .build();
    }

    public VocabularyWord getVocabularyWord(Integer userId, Integer wordId) {
        var word = vocabularyRepository.getWordForUser(userId, wordId);
        if (word.isEmpty()) {
            log.warn("Word with id -> {}, not found", wordId);
            throw new RequestException("Word by id not found");
        }

        var wordSentences = vocabularySentenceRepository.getAllByVocabulary_Id(wordId).stream()
                .limit(3)
                .map(VocabularySentence::getSentence)
                .toList();

        return VocabularyWord.builder()
                .translation(word.get().getTranslate())
                .alreadySaved(word.get().getSaved())
                .meaning(word.get().getMeaning())
                .word(word.get().getWord())
                .sentences(wordSentences)
                .build();
    }

    public List<UserWordDTO> getVocabularyForSentence(Integer userId, Integer sentenceId) {
        return vocabularySentenceRepository.findAllBySentenceId(sentenceId, userId);
    }

    public void deleteWordFromUserVocabulary(Integer userId, Integer wordId) {
        var vocabulary = userVocabularyRepository.findFirstByUser_IdAndWord_Id(userId, wordId);
        if (vocabulary.isEmpty()) throw new RequestException("Vocabulary not found");
        userVocabularyRepository.delete(vocabulary.get());
    }

    public void saveWordToUserVocabulary(Integer wordId, Integer userId) {
        var sentenceWord = vocabularyRepository.findById(wordId);
        if (sentenceWord.isEmpty()) {
            log.warn("Nothing found with this id");
            throw new RequestException("Nothing found with this id.");
        }
        var userReference = userRepository.getReferenceById(userId);

        var newWord = UserVocabulary.builder()
                .user(userReference)
                .word(sentenceWord.get())
                .build();

        userVocabularyRepository.save(newWord);
    }

    public List<UserWordDTO> getVocabulariesByKey(String key, Integer userId) {
        return vocabularyRepository.getBySearchKey(key, userId);
    }
}
