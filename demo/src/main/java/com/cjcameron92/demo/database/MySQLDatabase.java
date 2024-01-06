package com.cjcameron92.demo.database;

import com.cjcameron92.demo.config.MySQLConfig;
import com.cjcameron92.demo.storage.DemoStorage;
import gg.supervisor.api.Component;

@Component
public class MySQLDatabase {

    private final MySQLConfig config;

    public MySQLDatabase(MySQLConfig config, DemoStorage demoStorage) {
        this.config = config;

        // TODO: 2024-01-05
        System.out.println(config.hostName);
        System.out.println(config.port);
        System.out.println(config.username);
        System.out.println(config.password);
        System.out.println(config.database);

        config.properties.forEach((k, v) -> System.out.println(k + " - " +  v));
    }

    public MySQLConfig getConfig() {
        return config;
    }
}
