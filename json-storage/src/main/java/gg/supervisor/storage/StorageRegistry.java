package gg.supervisor.storage;

import java.util.Map;

@Deprecated
public interface StorageRegistry<T> extends Map<String, T> {

    void save();

    void load();

}
