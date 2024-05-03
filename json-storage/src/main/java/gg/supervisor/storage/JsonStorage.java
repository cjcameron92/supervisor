package gg.supervisor.storage;

public interface JsonStorage<T> {

    T read();

    void write();

    void reload();
}
