package gg.supervisor.core.repository.player;

import gg.supervisor.core.repository.PlayerRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class PlayerRepositoryListener<T> implements Listener {

    private final PlayerRepository<T> playerRepository;

    public PlayerRepositoryListener(PlayerRepository<T> playerRepository) {
        this.playerRepository = playerRepository;
    }

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        // Get player UUID as the key
        String playerKey = event.getUniqueId().toString();

        // Only load the profile if it already exists
        if (playerRepository.containsKey(playerKey)) {
            T profile = playerRepository.find(playerKey);
            if (profile != null) {
                playerRepository.save(playerKey, profile);
                Bukkit.getLogger().info("Loaded existing profile for player: " + event.getName());
            }
        } else {
            Bukkit.getLogger().info("No existing profile found for player: " + event.getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerKey = player.getUniqueId().toString();

        // Find the player's profile and save it
        T profile = playerRepository.find(playerKey);
        if (profile != null) {
            playerRepository.save(playerKey, profile);
            Bukkit.getLogger().info("Saved and removed profile for player: " + player.getName());
        }
    }
}
