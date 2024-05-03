package gg.supervisor.storage.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for defining SQL queries on methods.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Query {
    // SQL query string
    String value();

    // Optional parameter for enabling debugging on this query
    boolean debug() default false;
}