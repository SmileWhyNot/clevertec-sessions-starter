package vlad.kuchuk.sessioncleaner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vlad.kuchuk.service.SessionService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionCleanerScheduled {

    private final SessionCleanupProperties cleanupProperties;
    private final SessionService sessionService;

    @Scheduled(cron = "#{sessionCleanupProperties.getFrequency()}")
    public void clearExpiredSessions() {
        log.info("clearExpiredSessions called");
        if (cleanupProperties.isEnabled()) {
            sessionService.deleteAllExpiredSessions(LocalDateTime.now());
            log.info("clearExpiredSessions deleted sessions");
            return;
        }
        log.info("clearExpiredSessions disabled");
    }
}
