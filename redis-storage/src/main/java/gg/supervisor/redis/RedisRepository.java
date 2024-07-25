package gg.supervisor.redis;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface RedisRepository<T> {

    @NotNull CompletableFuture<T> find(@NotNull String key);

    @NotNull CompletableFuture<Void> save(@NotNull String key, @NotNull T type);

    @NotNull CompletableFuture<Boolean> delete(@NotNull String key);

    @NotNull CompletableFuture<Boolean> containsKey(@NotNull String key);

    @NotNull CompletableFuture<Collection<T>> values();

    @NotNull CompletableFuture<Collection<String>> keys();

}
