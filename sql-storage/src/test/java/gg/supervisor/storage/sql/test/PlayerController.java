package gg.supervisor.storage.sql.test;

import java.util.List;

public class PlayerController {

    private final PlayerSQLStorage playerSQLStorage;

    public PlayerController(PlayerSQLStorage playerSQLStorage) {
        this.playerSQLStorage = playerSQLStorage;
    }

    public List<Player> getPlayers() {
        return playerSQLStorage.findAll();
    }

    public Player getPlayerWhere(String s) {
        return playerSQLStorage.findAllWhere(s);
    }

}
