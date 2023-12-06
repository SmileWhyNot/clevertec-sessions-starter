package vlad.kuchuk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import vlad.kuchuk.model.AuthInfoImpl;
import vlad.kuchuk.model.Session;
import vlad.kuchuk.exception.SessionServiceNotAvailableException;

@Slf4j
@RequiredArgsConstructor
public class SessionProviderCommunicator {

    private final WebClient webClient;

    public Session getOrCreateSessionIfNotExist(AuthInfoImpl authInfo) {
        try {
            log.info("Entered SessionProviderCommunicator class");
            log.info("Sending request to Session-service...");
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("login", authInfo.login())
                            .build())
                    .retrieve()
                    .bodyToMono(Session.class)
                    .block();
        } catch (WebClientRequestException e) {
            log.error("WebClientRequestException caught. Session service is not available or incorrect url");
            throw new SessionServiceNotAvailableException("Request to Session-service took to much time. Service is not available or incorrect URL configured");
        }
    }
}