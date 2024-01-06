package com.cjcameron92.demo.config;

import gg.supervisor.api.Configuration;
import gg.supervisor.configuration.AbstractConfig;
import gg.supervisor.configuration.yaml.YamlConfigService;

import java.util.HashMap;
import java.util.Map;

@Configuration(fileName = "database.yml", service = YamlConfigService.class)
public class MySQLConfig extends AbstractConfig {

    public String hostName = "0.0.0.0";
    public int port = 3306;

    public String username = "admin";
    public String password = "l0calhost";

    public String database = "admin";

    public Map<String, String> properties = new HashMap<>() {{
        put("cachePrepStmts", "true");
        put("prepStmtCacheSize", "250");
        put("prepStmtCacheSqlLimit", "2048");
    }};
}
