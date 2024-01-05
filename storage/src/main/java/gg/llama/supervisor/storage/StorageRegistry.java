package gg.llama.supervisor.storage;

import java.util.Map;

public interface StorageRegistry<T> extends Map<String, T> {

    void save();

    void load();

}
