package org.zero.aienglish.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zero.aienglish.exception.SentenceUpdateException;
import org.zero.aienglish.model.Sentence;
import org.zero.aienglish.model.ThemeDTO;
import org.zero.aienglish.repository.ThemeRepository;
import org.zero.aienglish.service.ArtificialService;
import org.zero.aienglish.service.SentenceService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SentenceUpdate {
    private final ThemeRepository themeRepository;
    private final ArtificialService artificialService;
    private final SentenceService sentenceService;

    @Scheduled(fixedRate = 10_000)
    private void run() {
        var themeForUpdate = themeRepository.findThemeForUpdate();
        if (themeForUpdate.isEmpty()) {
            log.info("Theme for update not found");
            return;
        }

        var generatedSentenceList = artificialService.generateSentenceListByTheme(
                themeForUpdate.get()
        );
        saveGeneratedSentenceList(generatedSentenceList, themeForUpdate);

        themeRepository.updateLastUpdate(Instant.now(), themeForUpdate.get().getId());
        log.info("Sentence update finished");

    }

    private int saveGeneratedSentenceList(List<Sentence> generatedSentenceList, Optional<ThemeDTO> themeForUpdate) {
        int successfullySaved = 0;
        for (Sentence sentence : generatedSentenceList) {
            try {
                sentenceService.addSentence(sentence, themeForUpdate.get().getId());
                successfullySaved++;
            } catch (SentenceUpdateException sue) {
                log.warn(
                        "While scheduled words update for theme {}, occurred exception -> {}",
                        themeForUpdate.get().getTitle(), sue.getMessage()
                );
            }
        }
        return successfullySaved;
    }
}
