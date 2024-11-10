package com.vertmix.supervisor.repository.mongo.bukkit;

import com.vertmix.supervisor.repository.PlayerRepository;
import org.bukkit.entity.Player;

public interface BukkitMongoPlayerRepository<T> extends PlayerRepository<Player, T> {
}
