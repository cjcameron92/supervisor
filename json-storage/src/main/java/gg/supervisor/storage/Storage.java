package gg.supervisor.storage;

@Deprecated
public interface Storage<V> {

    void save();

    V load();

    V get();

    void update(V type);


}
