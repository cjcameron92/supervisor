package gg.supervisor.items;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface Item {

    default ItemStack buildItem(int qty) {
        return buildItem(qty, $ -> {});
    }

    ItemStack buildItem(int qty, Consumer<ItemMeta> consumer);

    @Nullable Consumer<InventoryClickEvent> getInventoryClickListener();

    @Nullable Consumer<PlayerInteractEvent> getInteractListener();

    @Nullable Consumer<PlayerAttemptPickupItemEvent> getPickupListener();

    @Nullable Consumer<PlayerDropItemEvent> getDropListener();
}
