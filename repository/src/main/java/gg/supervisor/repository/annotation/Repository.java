package gg.supervisor.repository.annotation;

import gg.supervisor.repository.ProxyHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {

    Class<? extends ProxyHandler<?>> service();

    Class<? extends Object> credentials() default String.class;

}
