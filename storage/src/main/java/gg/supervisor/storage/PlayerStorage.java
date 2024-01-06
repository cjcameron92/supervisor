package gg.supervisor.storage;

import java.util.UUID;

public interface PlayerStorage<V>  {

    V getPlayer(UUID uuid);

    void savePlayer(UUID uuid);


}
