package gg.supervisor.repository;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Repository<T> {

    @NotNull T find(@NotNull String key);

    void save(@NotNull String key, @NotNull T type);

    boolean delete(@NotNull String key);

    boolean containsKey(@NotNull String key);

    @NotNull Collection<T> values();

    @NotNull Collection<String> keys();

}
