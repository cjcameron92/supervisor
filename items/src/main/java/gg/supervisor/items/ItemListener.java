package gg.supervisor.items;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ItemListener implements Listener {

    private final NamespacedKey namespacedKey;

    public ItemListener(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.hasItem() && event.getItem() != null) {
            final ItemStack itemStack = event.getItem();
           findItem(itemStack).ifPresent(item -> {
               if (item.getInteractListener() != null) {
                   item.getInteractListener().accept(event);
               }
           });
        }
    }

    @EventHandler
    public void onItemPickup(@NotNull PlayerAttemptPickupItemEvent event) {
        final ItemStack itemStack = event.getItem().getItemStack();
        if (itemStack != null) {
            findItem(itemStack).ifPresent(item -> {
                if (item.getPickupListener() != null) {
                    item.getPickupListener().accept(event);
                }
            });
        }
    }

    @EventHandler
    public void onItemDrop(@NotNull PlayerDropItemEvent event) {
        final ItemStack itemStack = event.getItemDrop().getItemStack();
        if (itemStack != null) {
            findItem(itemStack).ifPresent(item -> {
                if (item.getDropListener() != null) {
                    item.getDropListener().accept(event);
                }
            });
        }
    }


    public @NotNull Optional<Item> findItem(@NotNull ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            if (container.has(namespacedKey, PersistentDataType.STRING)) {
                final String itemId = container.get(namespacedKey, PersistentDataType.STRING);
                return Optional.ofNullable(ItemRegistry.getItems().get(itemId));
            }
        }
        return Optional.empty();
    }

}
