package gg.supervisor.util.prompt;

import gg.supervisor.core.annotation.Component;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class represents a service for handling chat prompts.
 */
@Component
public class ChatPromptService implements Listener {

    private final Map<UUID, ChatScanner> chatScanners;

    /**
     * This class represents a service for handling chat prompts. Utilizes a record ChatScanner for managing prompt information.
     */
    public ChatPromptService() {
        this.chatScanners = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getProvidingPlugin(getClass()));// ensuring the event is being registered by the correct plugin
    }

    /**
     * Add a new ChatScanner to the chatScanners map with the provided UUID, expiration time, and consumer.
     *
     * @param uuid the UUID associated with the ChatScanner
     * @param expires the expiration time for the ChatScanner in seconds
     * @param consumer the consumer to handle the chat prompt input
     */
    public void create(UUID uuid, double expires, Consumer<String> consumer) {
        chatScanners.put(uuid, new ChatScanner(uuid, expires, consumer));
    }

    /**
     * Handles the chat event, cancels it if necessary, and passes the message content to the consumer.
     * If the associated ChatScanner for the player is expired or not found, the event is not processed.
     *
     * @param event The AsyncChatEvent to handle
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onChat(AsyncChatEvent event) {

        ChatScanner chatScanner = chatScanners.get(event.getPlayer().getUniqueId());
        chatScanners.remove(event.getPlayer().getUniqueId());

        if (chatScanner == null || chatScanner.isExpired()) {
            return;
        }

        event.setCancelled(true);

        chatScanner.consumer().accept(((TextComponent) event.message()).content());
    }

    public record ChatScanner(UUID uuid, double expires, Consumer<String> consumer) {

        public ChatScanner(UUID uuid, double expires, Consumer<String> consumer) {
            this.uuid = uuid;
            this.expires = System.currentTimeMillis() + (expires * 1000);
            this.consumer = consumer;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expires;
        }
    }
}