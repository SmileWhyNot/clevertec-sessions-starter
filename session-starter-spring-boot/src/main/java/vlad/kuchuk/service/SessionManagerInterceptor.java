package vlad.kuchuk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import vlad.kuchuk.annotation.SessionManager;
import vlad.kuchuk.exception.SessionManagerException;
import vlad.kuchuk.model.AuthInfo;
import vlad.kuchuk.model.AuthInfoImpl;
import vlad.kuchuk.model.Session;
import vlad.kuchuk.properties.SessionManagerProperties;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

            Set<String> blackList = Optional.of(annotation).stream()
                    .map(annot -> getBlackListsFromProviders(getResultedProviders(annot)))
                    .map(blackListsFromProviders -> combineBlackLists(annotation.blackList(), blackListsFromProviders))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());

            return Stream.of(args)
                    .filter(AuthInfo.class::isInstance)
                    .map(AuthInfo.class::cast)
                    .map(AuthInfo::login)
                    .filter(login -> isLoginNotInBlackList(login, blackList))
                    .findFirst()
                    .map(login -> {
                        if (isSessionProvidedInParams(args)) {
                            return args;
                        } else {
                            throw new SessionManagerException("Session not provided in method arguments");
                        }
                    })
                    .map(a -> providerCommunicator.getOrCreateSessionIfNotExist(new AuthInfoImpl(extractedLogin)))
                    .map(session -> {
                        try {
                            return method.invoke(originalBean, substituteArgs(args, session));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            log.error("method.invoke(originalBean, substituteArgs(args, session)); cause error = " + e.getCause());
                            throw new SessionManagerException("Failed to call method.invoke(originalBean, substituteArgs(args, session)); cause error = " + e.getCause());
                        }
                    })
                    .orElseThrow(() -> new SessionManagerException("Blacklist contains this login or it wasn't found in method arguments"));
        }
        return method.invoke(originalBean, args);
    }

    private Set<Class<? extends BlackListProvider>> getResultedProviders(SessionManager annotation) {
        Class<? extends BlackListProvider>[] providersFromAnnotation = annotation.blackListProviders();
        return Stream.concat(
                        Arrays.stream(providersFromAnnotation),
                        sessionManagerProperties.getBlackListProviders().stream()
                )
                .filter(provider -> annotation.includeDefaultBlackListSource() || !provider.equals(DefaultBlackListProvider.class))
                .collect(Collectors.toSet());
    }

    private Set<String> getBlackListsFromProviders(Set<Class<? extends BlackListProvider>> providers) {
        return providers.stream()
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
    }

    private Set<String> combineBlackLists(String[] paramBlackList, Set<String> blackLists) {
        return Stream.concat(
                Arrays.stream(paramBlackList),
                        blackLists.stream()
                )
                .collect(Collectors.toSet());
    }

    private String extractLoginFromArgs(Object[] args, Parameter[] parameters) {
        return IntStream.range(0, args.length)
                .mapToObj(i -> {
                    Parameter parameter = parameters[i];
                    if (AuthInfo.class.isAssignableFrom(parameter.getType())) {
                        AuthInfo login = (AuthInfo) args[i];
                        return Optional.ofNullable(login.login())
                                .filter(s -> !s.trim().isEmpty())
                                .orElseThrow(() -> new SessionManagerException("Login not found or empty in AuthInfo"));
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new SessionManagerException("Login not found in method arguments"));
    }

    private boolean isLoginNotInBlackList(String login, Set<String> blackList) {
        log.info("login = " + login + " blackList = " + blackList);
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