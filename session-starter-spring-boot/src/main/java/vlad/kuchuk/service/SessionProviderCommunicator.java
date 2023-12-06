package vlad.kuchuk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import vlad.kuchuk.model.AuthInfoImpl;
import vlad.kuchuk.model.Session;
import vlad.kuchuk.properties.SessionServiceNotAvailable;

@RequiredArgsConstructor
@Slf4j
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
            throw new SessionServiceNotAvailable("Request to Session-service took to much time. Service is not available");
        }
    }
}
