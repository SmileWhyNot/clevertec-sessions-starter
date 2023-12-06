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
    private final BeanFactory beanFactory;

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.isAnnotationPresent(SessionManager.class)) {
            SessionManager annotation = method.getAnnotation(SessionManager.class);
            String extractedLogin = extractLoginFromArgs(args, method.getParameters());

            Set<String> blackListsFromProviders = getBlackListsFromProviders(getResultedProviders(annotation));
            String[] blackListsFromAnnotation = annotation.blackList();
            Set<String> blackList = combineBlackLists(blackListsFromAnnotation, blackListsFromProviders);

            if (isLoginNotInBlackList(extractedLogin, blackList)
                    && isSessionProvidedInParams(args)) {
                Session session = providerCommunicator.getOrCreateSessionIfNotExist(new AuthInfoImpl(extractedLogin));

                return method.invoke(originalBean, substituteArgs(args, session));
            } else {
                throw new SessionManagerException("Method annotated with @SessionManager must contain args: class implementing AuthInfo; Session class." +
                        "Request login must not be in BlackList");
            }
        }
        return method.invoke(originalBean, args);
    }


    private Set<Class<? extends BlackListProvider>> getResultedProviders(SessionManager annotation) {
        Class<? extends BlackListProvider>[] providersFromAnnotation = annotation.blackListProviders();
        Set<Class<? extends BlackListProvider>> blackListProviders = Stream.concat(
                        Arrays.stream(providersFromAnnotation),
                        sessionManagerProperties.getBlackListProviders().stream()
                )
                .filter(provider -> annotation.includeDefaultBlackListSource() || !provider.equals(DefaultBlackListProvider.class))
                .collect(Collectors.toSet());

        return blackListProviders;
    }

    private Set<String> getBlackListsFromProviders(Set<Class<? extends BlackListProvider>> providers) {
        Set<String> collect = providers.stream()
                .map(providerClass -> {
                    try {
                        if (providerClass.equals(DefaultBlackListProvider.class)) {
                            DefaultBlackListProvider bean = beanFactory.getBean(DefaultBlackListProvider.class);
                            return bean.getBlackList();
                        } else {
                            BlackListProvider provider = providerClass.getDeclaredConstructor().newInstance();
                            return provider.getBlackList();
                        }
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        log.error("getBlackListsFromProviders caught error " + e.getMessage());
                        return Collections.<String>emptySet();
                    }
                })
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        return collect;
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
