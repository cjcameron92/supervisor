package gg.supervisor.items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.function.Consumer;

public interface ItemBuilder {

    ItemBuilder name(Component component);

    default ItemBuilder lore(Component... components) {
        return lore(List.of(components));
    }

    ItemBuilder lore(List<Component> component);

    ItemBuilder customModelData(Integer integer);

    ItemBuilder enchant(Enchantment enchantment, Integer level);

    ItemBuilder unsafeEnchant(Enchantment enchantment, Integer level);

    ItemBuilder hideEnchants();

    /**
     * Listeners
     **/

    ItemBuilder addInventoryClickListener(Consumer<InventoryClickEvent> consumer);

    ItemBuilder addInteractListener(Consumer<PlayerInteractEvent> consumer);

    ItemBuilder addPickupListener(Consumer<PlayerAttemptPickupItemEvent> consumer);

    ItemBuilder addDropListener(Consumer<PlayerDropItemEvent> consumer);

    Item build();

    static ItemBuilder newMenuItem(Material material) {
        return new ImmutableItemBuilder(material);
    }
    static ItemBuilder newBuilder(Material material, String itemId) {
        return new ImmutableItemBuilder(itemId, material);
    }


}
