package gg.supervisor.storage.sql;

import java.util.List;

public interface SQLStorage<T> {

    void save(T type);

    void delete(String key);

    T find(String key);

    List<T> findAll();
}
