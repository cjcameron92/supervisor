package com.cjcameron92.demo.storage;

import gg.supervisor.storage.json.JsonPlayerStorage;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class TeamPlayerStorage extends JsonPlayerStorage<TeamPlayerStorage.Team> {

    public TeamPlayerStorage(Plugin plugin) {
        super(Team.class, new File(plugin.getDataFolder(), "storage/teams/"),  player -> new Team(player.getName(), Collections.singletonList(player.getUniqueId().toString())));
    }

    record Team(String teamName, List<String> ids) {
    }
}
