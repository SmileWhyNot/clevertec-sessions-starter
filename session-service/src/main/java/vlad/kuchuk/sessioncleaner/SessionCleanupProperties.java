package vlad.kuchuk.sessioncleaner;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "session.cleanup")
@Slf4j
public class SessionCleanupProperties {
    private boolean enabled = true;
    private String frequency = "0 0 0 * * ?";

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        log.info("enable = " + enabled);
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
        log.info("frequency = " + frequency);
    }
}