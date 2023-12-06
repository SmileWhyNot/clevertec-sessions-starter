package vlad.kuchuk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vlad.kuchuk.properties.SessionManagerProperties;

import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class DefaultBlackListProvider implements BlackListProvider {

    private final SessionManagerProperties sessionManagerProperties;

    @Override
    public Set<String> getBlackList() {
        log.info("Blacklist found " + sessionManagerProperties.getBlackList());
        return sessionManagerProperties.getBlackList();
    }
}
