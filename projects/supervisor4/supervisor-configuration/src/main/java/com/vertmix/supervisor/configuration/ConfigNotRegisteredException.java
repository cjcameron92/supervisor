package com.vertmix.supervisor.configuration;

public class ConfigNotRegisteredException extends RuntimeException {

    public ConfigNotRegisteredException(String str) {
        super(str);
    }
}
