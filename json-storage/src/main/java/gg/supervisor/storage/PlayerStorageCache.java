package gg.supervisor.storage;

import java.util.UUID;

@Deprecated
public interface PlayerStorageCache<T> {

    T getFromCache(UUID uuid);

}
