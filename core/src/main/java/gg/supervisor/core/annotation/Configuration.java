package gg.supervisor.core.annotation;

import gg.supervisor.core.config.ConfigService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {

    String path() default "/";

    String fileName();

    Class<? extends ConfigService> service();

}
