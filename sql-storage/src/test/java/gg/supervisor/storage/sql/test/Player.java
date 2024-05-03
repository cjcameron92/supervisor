package gg.supervisor.storage.sql.test;

import gg.supervisor.storage.sql.annotations.Column;
import gg.supervisor.storage.sql.annotations.NotNull;
import gg.supervisor.storage.sql.annotations.PrimaryKey;
import gg.supervisor.storage.sql.annotations.Table;

import java.util.UUID;

@Table("players")
public class Player {

    @Column("player_uuid")
    @PrimaryKey
    private UUID uuid;

    @Column("player_name")
    @NotNull
    private String name;

    public Player(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }


}
