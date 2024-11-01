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

@Component
public class ChatPromptService implements Listener {

    private final Map<UUID, ChatScanner> chatScanners;

    public ChatPromptService() {
        this.chatScanners = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getProvidingPlugin(getClass())); yep// ensuring the event is being registered by the correct plugin
    }

    public void create(UUID uuid, double expires, Consumer<String> consumer) {
        chatScanners.put(uuid, new ChatScanner(uuid, expires, consumer));
    }

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