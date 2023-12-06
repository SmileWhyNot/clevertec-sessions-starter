package vlad.kuchuk.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import vlad.kuchuk.properties.SessionManagerProperties;

@RequiredArgsConstructor
@Slf4j
public class ApplicationStartListener implements ApplicationListener<ContextRefreshedEvent> {

    private final SessionManagerProperties sessionManagerProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Default session properties: blackList - {}, SessionProviderUrl - {}, " +
                        " isEnabled - {},  blacklistProviders - {}",
                sessionManagerProperties.getBlackList(),
                sessionManagerProperties.getSessionProviderUrl(),
                sessionManagerProperties.getEnable(),
                sessionManagerProperties.getBlackListProviders());
    }
}