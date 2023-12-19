package vlad.kuchuk.sessioncleaner;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@Slf4j
@ConfigurationProperties(prefix = "session.cleanup")
public class SessionCleanupProperties {

    private boolean enabled = true;
    private String frequency = "0 0 0 * * ?";

    @PostConstruct
    public void init() {
        log.info("Initialized SessionCleanupProperties with enable = {} and frequency = {}", enabled, frequency);
    }
}
