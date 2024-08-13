package gg.supervisor.redis;

import java.util.List;

public interface RedisRepository<T> {

    void get(String key, T type);

    void put(String key, T type);

    void remove(String key);

    List<String> keys();

    List<T> values();
}
