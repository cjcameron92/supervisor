package gg.supervisor.repository.itemstack;

import gg.supervisor.core.loader.SupervisorLoader;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class ItemStackStore<T> implements ItemStore<T> {

    private final Class<T> entityType;

    private final NamespacedKey namespacedKey;

    public ItemStackStore(Class<T> entityType, Plugin plugin) {
        this.entityType = entityType;
        this.namespacedKey = new NamespacedKey(plugin, "itemstore_" + entityType.getSimpleName().toLowerCase());
    }

    @Override
    public T get(String key) {
        return null;
    }

    @Override
    public void save(String key, T value) {

    }

    @Override
    public void delete(String key) {

    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public Map<String, T> values() {
        return null;
    }

    @Override
    public T get(ItemStack key) {
        if (key == null) return null;
        if (!key.hasItemMeta()) return null;
        final ItemMeta itemMeta = key.getItemMeta();
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(namespacedKey, PersistentDataType.STRING)) {
            final String json = container.get(namespacedKey, PersistentDataType.STRING);
            return SupervisorLoader.GSON.fromJson(json, entityType);
        }
        return null;
    }

    @Override
    public void save(ItemStack key, T value) {
        if (key == null) return;
        if (!key.hasItemMeta()) return;
        final ItemMeta itemMeta = key.getItemMeta();
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(namespacedKey, PersistentDataType.STRING, SupervisorLoader.GSON.toJson(value));
        key.setItemMeta(itemMeta);
    }

    @Override
    public void delete(ItemStack key) {
        if (key == null) return;
        if (!key.hasItemMeta()) return;
        final ItemMeta itemMeta = key.getItemMeta();
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.remove(namespacedKey);
        key.setItemMeta(itemMeta);
    }

    @Override
    public boolean containsKey(ItemStack key) {
        if (key == null) return false;
        if (!key.hasItemMeta()) return false;
        final ItemMeta itemMeta = key.getItemMeta();
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(namespacedKey, PersistentDataType.STRING);
    }
}
