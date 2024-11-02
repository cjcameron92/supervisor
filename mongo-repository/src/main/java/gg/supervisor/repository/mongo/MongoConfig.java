package gg.supervisor.repository.mongo;


import gg.supervisor.configuration.yaml.YamlConfigService;
import gg.supervisor.core.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration(fileName = "mongo.yml", service = YamlConfigService.class)
public class MongoConfig {

    public String mongoUri = "mongodb://localhost:27017";
    public String database = "admin";
    public Map<String, String> collectionTypes = new HashMap<>();

}
