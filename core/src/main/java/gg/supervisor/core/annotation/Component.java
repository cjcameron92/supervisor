package gg.supervisor.core.annotation;

import gg.supervisor.core.util.ServicePriority;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    ServicePriority priority() default ServicePriority.NORMAL;

}