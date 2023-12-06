package vlad.kuchuk.annotation;

import vlad.kuchuk.service.BlackListProvider;
import vlad.kuchuk.service.DefaultBlackListProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionManager {
    String[] blackList() default {};
    boolean includeDefaultBlackListSource() default true;
    Class<? extends BlackListProvider>[] blackListProviders() default DefaultBlackListProvider.class;
}