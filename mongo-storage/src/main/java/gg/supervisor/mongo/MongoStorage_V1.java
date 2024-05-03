package gg.supervisor.mongo;

import java.util.List;

public interface MongoStorage_V1<T> {

    void save(String id, T type);

    boolean delete(String id);

    T find(String id);

    List<T> findAll();
}
