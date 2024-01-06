package gg.supervisor.items;

import com.google.common.base.Preconditions;
import gg.supervisor.api.Services;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ImmutableItemBuilder implements ItemBuilder {

    private final NamespacedKey namespacedKey = new NamespacedKey(Services.loadIfPresent(Plugin.class), "spvitems");
    private final String itemId;
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    private Consumer<InventoryClickEvent> inventoryClickListener;
    private Consumer<PlayerInteractEvent> interactListener;
    private Consumer<PlayerAttemptPickupItemEvent> pickupListener;
    private Consumer<PlayerDropItemEvent> dropListener;

    public ImmutableItemBuilder(Material material) {
        this(material, 1);
    }

    public ImmutableItemBuilder(Material material, int qty) {
        this.itemId = UUID.randomUUID().toString();
        this.itemStack = new ItemStack(material, qty);
        this.itemMeta = itemStack.getItemMeta();
    }
    public ImmutableItemBuilder( @NotNull String itemId, @NotNull Material material) {
        this(itemId,new ItemStack(material, 1));
    }


    public ImmutableItemBuilder(@NotNull String itemId, @NotNull ItemStack itemStack) {
        this.itemId = itemId;
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();

    }

    @Override
    public @NotNull ItemBuilder name(@NotNull Component component) {
        this.itemMeta.displayName(component);
        return this;
    }

    @Override
    public @NotNull ItemBuilder lore(@NotNull List<Component> lore) {
        this.itemMeta.lore(lore);
        return this;
    }

    @Override
    public @NotNull ItemBuilder customModelData(@NotNull Integer integer) {
        Preconditions.checkArgument(integer > 0);
        this.itemMeta.setCustomModelData(integer);
        return this;
    }

    @Override
    public @NotNull ItemBuilder enchant(@NotNull Enchantment enchantment, @NotNull Integer level) {
        this.itemMeta.addEnchant(enchantment, level, false);
        return this;
    }

    @Override
    public @NotNull ItemBuilder unsafeEnchant(@NotNull Enchantment enchantment, @NotNull Integer level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    @Override
    public @NotNull ItemBuilder hideEnchants() {
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    @Override
    public @NotNull ItemBuilder addInventoryClickListener(@NotNull Consumer<InventoryClickEvent> consumer) {
        this.inventoryClickListener = consumer;
        return this;
    }

    @Override
    public @NotNull ItemBuilder addInteractListener(@NotNull Consumer<PlayerInteractEvent> consumer) {
        this.interactListener = consumer;
        return this;
    }

    @Override
    public @NotNull ItemBuilder addPickupListener(@NotNull Consumer<PlayerAttemptPickupItemEvent> consumer) {
        this.pickupListener = consumer;
        return this;
    }

    @Override
    public @NotNull ItemBuilder addDropListener(@NotNull Consumer<PlayerDropItemEvent> consumer) {
        this.dropListener = consumer;
        return this;
    }

    @Override
    public @NotNull Item build() {
        final ItemStack clone = this.itemStack.clone();
        clone.setItemMeta(itemMeta);

        final Item item =  new ImmutableItem(namespacedKey, itemId, clone, inventoryClickListener, interactListener, pickupListener, dropListener);

        if (Services.loadIfPresent(ItemListener.class) == null) {
            ItemListener itemListener = new ItemListener(namespacedKey);
            Bukkit.getPluginManager().registerEvents(itemListener, Services.loadIfPresent(Plugin.class));
            Services.register(ItemListener.class, itemListener);
        }

        ItemRegistry.getItems().put(itemId, item);
        // register

        return item;
    }

    static class ImmutableItem implements Item {

        private final NamespacedKey namespacedKey;
        private final ItemStack itemStack;
        private final Consumer<InventoryClickEvent> inventoryClickListener;

        private final Consumer<PlayerInteractEvent> interactListener;
        private final Consumer<PlayerAttemptPickupItemEvent> pickupListener;
        private final Consumer<PlayerDropItemEvent> dropListener;

        private final String itemId;

        public ImmutableItem(@NotNull NamespacedKey namespacedKey, @NotNull String itemId, @NotNull ItemStack itemStack, Consumer<InventoryClickEvent> inventoryClickListener, Consumer<PlayerInteractEvent> interactListener, Consumer<PlayerAttemptPickupItemEvent> pickupListener, Consumer<PlayerDropItemEvent> dropListener) {
            this.namespacedKey = namespacedKey;
            this.itemId = itemId;
            this.itemStack = itemStack;
            this.inventoryClickListener = inventoryClickListener;
            this.interactListener = interactListener;
            this.pickupListener = pickupListener;
            this.dropListener = dropListener;
        }

        @Override
        public @NotNull ItemStack buildItem(int qty, @NotNull Consumer<ItemMeta> consumer) {
            final ItemStack clone = this.itemStack.clone();
            if (itemId != null) {
                final ItemMeta meta = clone.getItemMeta();
                meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, itemId);
                consumer.accept(meta);
                clone.setItemMeta(meta);
            }
            clone.setAmount(qty);
            return clone;
        }

        @Override
        @Nullable
        public Consumer<InventoryClickEvent> getInventoryClickListener() {
            return this.inventoryClickListener;
        }

        @Override
        @Nullable
        public Consumer<PlayerInteractEvent> getInteractListener() {
            return this.interactListener;
        }

        @Override
        @Nullable
        public Consumer<PlayerAttemptPickupItemEvent> getPickupListener() {
            return this.pickupListener;
        }

        @Override
        @Nullable
        public Consumer<PlayerDropItemEvent> getDropListener() {
            return this.dropListener;
        }
    }
}
