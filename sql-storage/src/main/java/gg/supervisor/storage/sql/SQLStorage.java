package gg.supervisor.storage.sql.test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SQLStorage<T> {

    CompletableFuture<Void> save(T type);

    CompletableFuture<Void> delete(String key);

    CompletableFuture<T> find(String key);

    CompletableFuture<List<T>> findAll();
}
