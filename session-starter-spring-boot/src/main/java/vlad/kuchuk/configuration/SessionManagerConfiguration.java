package vlad.kuchuk.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.function.client.WebClient;
import vlad.kuchuk.bpp.SessionManagerBeanPostProcessor;
import vlad.kuchuk.listener.ApplicationStartListener;
import vlad.kuchuk.properties.SessionManagerProperties;
import vlad.kuchuk.service.DefaultBlackListProvider;
import vlad.kuchuk.service.SessionProviderCommunicator;


@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableAsync
@RequiredArgsConstructor
@EnableConfigurationProperties(SessionManagerProperties.class)
@ConditionalOnProperty(value = "session.manager.enable", havingValue = "true")
public class SessionManagerConfiguration {

    @Bean
    public ApplicationStartListener applicationStartListener(SessionManagerProperties sessionManagerProperties) {
        return new ApplicationStartListener(sessionManagerProperties);
    }

    @Bean
    public SessionManagerBeanPostProcessor sessionManagerBeanPostProcessor() {
        return new SessionManagerBeanPostProcessor();
    }

    @Bean
    public SessionProviderCommunicator sessionProviderCommunicator(SessionManagerProperties sessionManagerProperties) {
        return new SessionProviderCommunicator(webClient(sessionManagerProperties));
    }

    @Bean
    public DefaultBlackListProvider blackListProvider(SessionManagerProperties sessionManagerProperties) {
        return new DefaultBlackListProvider(sessionManagerProperties);
    }

    @Bean
    public WebClient webClient(SessionManagerProperties sessionManagerProperties) {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(sessionManagerProperties.getSessionProviderUrl())
                .build();
    }
}
