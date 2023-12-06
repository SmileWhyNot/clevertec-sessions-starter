package vlad.kuchuk.bpp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import vlad.kuchuk.annotation.SessionManager;
import vlad.kuchuk.properties.SessionManagerProperties;
import vlad.kuchuk.service.SessionManagerInterceptor;
import vlad.kuchuk.service.SessionProviderCommunicator;

import java.lang.reflect.Constructor;
import java.util.*;

@Slf4j
public class SessionManagerBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private final Map<String, Class<?>> beanNamesWithAnnotatedMethods = new HashMap<>();
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        log.info("Entered SessionManagerBeanPostProcessor");
        log.info("Method postProcessBeforeInitialization params: bean=" + bean + " beanName=" + beanName);
        Class<?> clazz = bean.getClass();
        Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(SessionManager.class))
                .forEach(method -> beanNamesWithAnnotatedMethods.put(beanName, clazz));
        log.info("beanNamesWithAnnotatedMethods" + beanNamesWithAnnotatedMethods.values());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        log.info("Entered postProcessAfterInitialization");
        log.info("Method postProcessAfterInitialization params: bean=" + bean + " beanName=" + beanName);
        return Optional.ofNullable(beanNamesWithAnnotatedMethods.get(beanName))
                .map(clazz -> getSessionProxy(bean))
                .orElse(bean);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private Object getSessionProxy(Object bean) {
        SessionProviderCommunicator sessionProviderCommunicator = beanFactory.getBean(SessionProviderCommunicator.class);
        SessionManagerProperties sessionManagerProperties = beanFactory.getBean(SessionManagerProperties.class);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(bean.getClass());
        enhancer.setCallback(new SessionManagerInterceptor(bean, sessionProviderCommunicator, sessionManagerProperties, beanFactory));

        return isPresentDefaultConstructor(bean)
                ? enhancer.create()
                : enhancer.create(getNotDefaultConstructorArgTypes(bean), getNotDefaultConstructorArgs(bean));
    }

    private boolean isPresentDefaultConstructor(Object bean) {
        return Arrays.stream(bean.getClass().getConstructors())
                .anyMatch(constructor -> constructor.getParameterCount() == 0);
    }

    private Class<?>[] getNotDefaultConstructorArgTypes(Object object) {
        return Arrays.stream(object.getClass().getConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .map(Constructor::getParameterTypes)
                .orElseThrow(IllegalArgumentException::new);
    }

    private Object[] getNotDefaultConstructorArgs(Object object) {
        Class<?>[] constructorArgTypes = getNotDefaultConstructorArgTypes(object);
        return Arrays.stream(constructorArgTypes)
                .map(beanFactory::getBean)
                .toArray();
    }
}
