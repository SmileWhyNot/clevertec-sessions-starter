package vlad.kuchuk.sessioncleaner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vlad.kuchuk.service.SessionService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "session.cleanup", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SessionCleanerScheduled {

    private final SessionService sessionService;

    @Scheduled(cron = "#{sessionCleanupProperties.getFrequency()}")
    public void clearExpiredSessions() {
        log.info("clearExpiredSessions called");
        sessionService.deleteAllExpiredSessions(LocalDateTime.now());
        log.info("clearExpiredSessions deleted sessions");
    }
}