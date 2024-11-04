package gg.supervisor.core.repository;

import java.util.Collection;

public interface Repository<T> {

    void save(String key, T type);

    T find(String key);

    void delete(String key);

    boolean containsKey(String key);

    Collection<T> values();

    Collection<String> keys();
}
