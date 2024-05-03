package gg.supervisor.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configuration {

    String path() default "/";

    String fileName();

    Class<? extends ConfigService> service();

    boolean verbose() default false;

}
