package com.vertmix.supervisor.repository.bukkit;

import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.repository.PlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerRepositoryListener<T> implements Listener {

    private final CoreProvider<Plugin> provider;
    private final PlayerRepository<Player, T> playerRepository;

    public PlayerRepositoryListener(CoreProvider<Plugin> provider, PlayerRepository<Player, T> playerRepository) {
        this.provider = provider;
        this.playerRepository = playerRepository;
    }

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        if (playerRepository.containsKey(event.getUniqueId())) {
            T data = playerRepository.find(event.getUniqueId());
            if (data != null) {
                playerRepository.save(event.getUniqueId(), data);
                Bukkit.getLogger().info("Loaded existing profile for player: " + event.getName());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerKey = player.getUniqueId().toString();

        Bukkit.getScheduler().runTaskAsynchronously(provider.getSource(), () -> {
            // Find the player's profile and save it
            T profile = playerRepository.find(playerKey);
            if (profile != null) {
                playerRepository.save(playerKey, profile);
                Bukkit.getLogger().info("Saved and removed profile for player: " + player.getName());
            }
        });
    }
}
