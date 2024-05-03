package gg.supervisor.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Storage {

    String fileName() default "";

    Class<?> type();

    Class<? extends StorageService> service();

    Class<?> config() default Object.class;
}
