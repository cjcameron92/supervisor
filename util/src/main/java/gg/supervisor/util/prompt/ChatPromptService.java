package gg.supervisor.util.prompt;

import gg.supervisor.core.annotation.Component;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A service for handling chat prompts in Minecraft, enabling the ability to
 * process player chat messages as responses to specific prompts within a
 * defined expiration period.
 */
@Component
public class ChatPromptService implements Listener {

    /**
     * A map of active chat scanners, keyed by player UUID.
     */
    private final Map<UUID, ChatScanner> chatScanners;

    /**
     * The plugin instance that owns this service.
     */
    private final Plugin plugin;

    /**
     * Constructs a new {@code ChatPromptService} and registers the chat event listener.
     */
    public ChatPromptService() {
        this.chatScanners = new HashMap<>();
        this.plugin = JavaPlugin.getProvidingPlugin(getClass());

        // Registering the chat event listener
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    public void cancel(UUID uuid) {
        Optional.ofNullable(chatScanners.get(uuid)).ifPresent(chatScanner -> {
            chatScanner.cancel();
            chatScanners.remove(uuid);
        });
    }

    /**
     * Creates a new chat prompt for the specified player.
     *
     * @param uuid     the UUID of the player to listen for chat messages from
     * @param expires  the expiration time in ticks
     * @param consumer a {@code Consumer} to process the player's input
     * @param expired  a {@code Runnable} to execute if the prompt expires
     */
    public void create(UUID uuid, double expires, Consumer<String> consumer, Runnable expired) {
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            chatScanners.remove(uuid);
            expired.run();
        }, (long) Math.ceil(expires * 20L));

        chatScanners.put(uuid, new ChatScanner(uuid, expires, consumer, task.getTaskId()));
    }

    /**
     * Creates a new chat prompt for the specified player, without an expiration action.
     *
     * @param uuid     the UUID of the player to listen for chat messages from
     * @param expires  the expiration time in ticks
     * @param consumer a {@code Consumer} to process the player's input
     */
    public void create(UUID uuid, double expires, Consumer<String> consumer) {
        create(uuid, expires, consumer, () -> {
        });
    }

    /**
     * Handles the {@link AsyncChatEvent}, capturing chat messages for players with active prompts.
     *
     * @param event the {@code AsyncChatEvent} triggered when a player sends a chat message
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onChat(AsyncChatEvent event) {
        ChatScanner chatScanner = chatScanners.get(event.getPlayer().getUniqueId());
        chatScanners.remove(event.getPlayer().getUniqueId());

        if (chatScanner == null || chatScanner.isExpired()) {
            return;
        }

        chatScanner.cancel();
        event.setCancelled(true);

        chatScanner.consumer().accept(((TextComponent) event.message()).content());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onLeave(PlayerQuitEvent event) {

        final ChatScanner chatScanner = chatScanners.get(event.getPlayer().getUniqueId());
        chatScanner.cancel();

        chatScanners.remove(event.getPlayer().getUniqueId());

    }

    /**
     * Represents a single chat scanner that listens for a player's chat input.
     *
     * @param uuid     the UUID of the player
     * @param expires  the expiration timestamp in milliseconds
     * @param consumer the consumer to process the player's chat input
     * @param task     the ID of the scheduled expiration task
     */
    public record ChatScanner(UUID uuid, double expires, Consumer<String> consumer, int task) {

        /**
         * Constructs a new {@code ChatScanner}.
         *
         * @param uuid     the UUID of the player
         * @param expires  the expiration timestamp in milliseconds
         * @param consumer the consumer to process the player's chat input
         * @param task     the ID of the scheduled expiration task
         */
        public ChatScanner(UUID uuid, double expires, Consumer<String> consumer, int task) {
            this.uuid = uuid;
            this.expires = System.currentTimeMillis() + (expires * 1000);
            this.consumer = consumer;
            this.task = task;
        }

        /**
         * Checks if the chat prompt has expired.
         *
         * @return {@code true} if the chat prompt has expired; {@code false} otherwise
         */
        public boolean isExpired() {
            return System.currentTimeMillis() >= expires;
        }

        /**
         * Cancels the expiration task for this chat scanner.
         */
        public void cancel() {
            if (task >= 0)
                Bukkit.getScheduler().cancelTask(task);
        }
    }
}
