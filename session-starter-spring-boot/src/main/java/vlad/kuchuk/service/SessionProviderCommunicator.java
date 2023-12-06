package vlad.kuchuk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vlad.kuchuk.model.AuthInfoImpl;
import vlad.kuchuk.model.Session;

@RequiredArgsConstructor
@Slf4j
public class SessionProviderCommunicator {

    private final WebClient webClient;

    public Mono<Session> getOrCreateSessionIfNotExist(AuthInfoImpl authInfo) {
        log.info("Entered SessionProviderCommunicator class");
        log.info("Sending request to Session-service...");
        return webClient.post()
                    .bodyValue(authInfo)
                    .retrieve()
                    .bodyToMono(Session.class);
    }
}
