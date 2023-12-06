package vlad.kuchuk.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import vlad.kuchuk.service.BlackListProvider;

import java.util.HashSet;
import java.util.Set;

@Data
@ConfigurationProperties(prefix = "session.manager")
@Slf4j
public class SessionManagerProperties {
    private Boolean enable;
    private String sessionProviderUrl;
    private final Set<String> blackList = new HashSet<>();
    private final Set<Class<? extends BlackListProvider>> blackListProviders = new HashSet<>();

    public void setSessionProviderUrl(String sessionProviderUrl) {
        this.sessionProviderUrl = sessionProviderUrl == null ?
                "http://localhost:8181/api/v1/sessions" : sessionProviderUrl;

        log.info("sessionProviderUrl is set to" + this.sessionProviderUrl);
    }

    public void setEnable(Boolean enable) {
        this.enable = enable != null && enable;
    }
}
