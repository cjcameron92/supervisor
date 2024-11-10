package com.vertmix.supervisor.repository.mongo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MongoContext {

    String collection();

    String database();
}
