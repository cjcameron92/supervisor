package gg.supervisor.storage.sql.test;

import gg.supervisor.api.Storage;
import gg.supervisor.storage.sql.SQLStorageService;
import gg.supervisor.storage.sql.config.SQLConfig;
import gg.supervisor.storage.sql.SQLStorage;
import gg.supervisor.storage.sql.annotations.Query;


@Storage(
        service = SQLStorageService.class,
        config = SQLConfig.class,
        type = Player.class
)
public interface PlayerSQLStorage extends SQLStorage<Player> {

    @Query("SELECT * FROM players WHERE name='?';")
    Player findAllWhere(String name);
}
