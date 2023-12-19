package vlad.kuchuk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vlad.kuchuk.exception.SessionServiceNotAvailableException;
import vlad.kuchuk.model.AuthInfoImpl;
import vlad.kuchuk.model.Session;
import vlad.kuchuk.properties.SessionManagerProperties;

@Slf4j
@RequiredArgsConstructor
public class SessionProviderCommunicator {

    private final RestTemplate restTemplate;
    private final SessionManagerProperties sessionManagerProperties;

    public Session getOrCreateSessionIfNotExist(AuthInfoImpl authInfo) {
        try {
            log.info("Entered SessionProviderCommunicator class");
            log.info("Sending request to Session-service...");

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(sessionManagerProperties.getSessionProviderUrl())
                                                                  .queryParam("login", authInfo.login());

            return restTemplate.getForObject(uriBuilder.toUriString(), Session.class);
        } catch (RestClientException e) {
            log.error("RestClientException caught. Session service is not available or incorrect url");
            throw new SessionServiceNotAvailableException("Request to Session-service took too much time. Service is not available or incorrect URL configured");
        }
    }
}