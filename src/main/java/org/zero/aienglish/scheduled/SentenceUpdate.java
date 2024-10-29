package org.zero.aienglish.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zero.aienglish.exception.SentenceUpdateException;
import org.zero.aienglish.repository.ThemeRepository;
import org.zero.aienglish.service.ArtificialService;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SentenceUpdate {
    private final ThemeRepository themeRepository;
    private final ArtificialService artificialService;

    @Scheduled(fixedRate = 5_000)
    private void run() {
        var themeForUpdate = themeRepository.findThemeForUpdate();
        if (themeForUpdate.isEmpty()) {
            log.info("Theme for update not found");
            return;
        }

        int successfullySaved;
        try {
            successfullySaved = artificialService.generateSentenceListByTheme(themeForUpdate.get());
        } catch (SentenceUpdateException sue) {
            log.warn(
                    "While scheduled sentence update for theme {}, occurred exception -> {}",
                    themeForUpdate.get().getTitle(), sue.getMessage()
            );
            return;
        }

        if (successfullySaved == 0) {
            log.warn("Zero sentences saved");
          return;
        }
        themeRepository.updateLastUpdate(Instant.now(), themeForUpdate.get().getId());
        log.info("Sentence update successful");

    }
}
