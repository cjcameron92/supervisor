package gg.supervisor.redis.example;

import java.util.UUID;

public class Player {

    private final UUID uuid;
    private String name;

    public Player(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

}
