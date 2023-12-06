package vlad.kuchuk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vlad.kuchuk.annotation.SessionManager;
import vlad.kuchuk.exception.SessionManagerException;
import vlad.kuchuk.model.AuthInfo;
import vlad.kuchuk.model.AuthInfoImpl;
import vlad.kuchuk.model.Session;
import vlad.kuchuk.properties.SessionManagerProperties;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
public class SessionManagerInterceptor implements MethodInterceptor {

    private final Object originalBean;
    private final SessionProviderCommunicator providerCommunicator;
    private final SessionManagerProperties sessionManagerProperties;

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.isAnnotationPresent(SessionManager.class)) {
            SessionManager annotation = method.getAnnotation(SessionManager.class);
            String extractedLogin = extractLoginFromArgs(args, method.getParameters());

            CompletableFuture<Void> asyncWork = Mono.fromCallable(() -> getResultedProviders(annotation.includeDefaultBlackListSource(), annotation.blackListProviders()))
                    .flatMap(blackLists -> Mono.fromCallable(() -> getBlackListsFromProviders(blackLists)))
                    .map(blackLists -> combineBlackLists(annotation.blackList(), blackLists))
                    .flatMapMany(blackList -> Flux.fromArray(args)
                            .filter(AuthInfo.class::isInstance)
                            .map(AuthInfo.class::cast)
                            .filter(authInfo -> {
                                if (!isLoginNotInBlackList(extractedLogin, blackList) || !isSessionProvidedInParams(args)) {
                                    throw new SessionManagerException("You haven't provided Session in params or your Login is in blacklist");
                                }
                                return true;
                            })
                            .flatMap(authInfo -> providerCommunicator.getOrCreateSessionIfNotExist(new AuthInfoImpl(extractedLogin)))
                            .switchIfEmpty(Mono.error(new SessionManagerException("Method annotated with @SessionManager must include Session in params, and object that implements AuthInfo")))
                            .doOnNext(session -> {
                                try {
                                    method.invoke(originalBean, substituteArgs(args, session));
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new SessionManagerException("intercept exception" + e.getMessage());
                                }
                            })
                    )
                    .then().toFuture();
            asyncWork.join();
            return null;
        }
        return method.invoke(originalBean, args);
    }



    private Set<Class<? extends BlackListProvider>> getResultedProviders(boolean isDefaultBlackListSourceEnabled,
                                                                         Class<? extends BlackListProvider>[] providers) {
        return Stream.concat(
                        Arrays.stream(providers),
                        sessionManagerProperties.getBlackListProviders().stream()
                )
                .filter(provider -> isDefaultBlackListSourceEnabled || !provider.equals(DefaultBlackListProvider.class))
                .collect(Collectors.toSet());
    }

    private Set<String> getBlackListsFromProviders(Set<Class<? extends BlackListProvider>> providers) {
        return providers.stream()
                .map(providerClass -> {
                    try {
                        BlackListProvider provider = providerClass.getDeclaredConstructor().newInstance();
                        return provider.getBlackList();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        log.error(e.getMessage());
                        return Collections.<String>emptySet();
                    }
                })
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Set<String> combineBlackLists(String[] paramBlackList, Set<String> blackLists) {
        Set<String> finalBlackLists = new HashSet<>(Arrays.asList(paramBlackList));
        finalBlackLists.addAll(blackLists);
        return finalBlackLists;
    }

    private String extractLoginFromArgs(Object[] args, Parameter[] parameters) {
        for (int i = 0; i < args.length; i++) {
            Parameter parameter = parameters[i];
            if (AuthInfo.class.isAssignableFrom(parameter.getType())) {
                AuthInfo login = (AuthInfo) args[i];
                return Optional.ofNullable(login.login())
                        .orElseThrow(() -> new SessionManagerException("Login not found in AuthInfo"));
            }
        }
        throw new SessionManagerException("Login not found in method arguments");
    }

    private boolean isLoginNotInBlackList(String login, Set<String> blackList) {
        log.info("login =" + login + "blackList = " + blackList);
        return !blackList.contains(login);
    }

    private boolean isSessionProvidedInParams(Object[] args) {
        return Arrays.stream(args).anyMatch(Session.class::isInstance);
    }

    private Object[] substituteArgs(Object[] args, Session session) {
        return Arrays.stream(args)
                .map(arg ->
                        arg instanceof Session ?
                                session : arg
                ).toArray();
    }

}
