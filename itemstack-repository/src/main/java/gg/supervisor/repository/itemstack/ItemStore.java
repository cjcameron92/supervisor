package gg.supervisor.repository.itemstack;

import gg.supervisor.core.repository.store.Store;
import org.bukkit.inventory.ItemStack;

public interface ItemStore<T> extends Store<T> {

    /**
     * Retrieves an item from the store based on the provided key.
     * <p>
     * This method is used to look up and retrieve the corresponding value for a given key.
     * If the key does not exist, it returns {@code null}. It is recommended to handle the
     * {@code null} scenario to prevent runtime exceptions.
     * </p>
     *
     * @param key The key used to identify the item.
     * @return The value associated with the key, or {@code null} if the key does not exist.
     */
    T get(ItemStack key);

    /**
     * Saves or updates an item in the store with the provided key-value pair.
     * <p>
     * This method either adds a new entry or updates an existing entry in the store.
     * The key serves as the identifier, while the value is the data being stored.
     * It is commonly used when adding new data or updating existing data.
     * </p>
     *
     * @param key   The key used to identify the item.
     * @param value The value to be associated with the key.
     */
    void save(ItemStack key, T value);

    /**
     * Deletes an item from the store based on the provided key.
     * <p>
     * This method removes the key-value pair associated with the provided key.
     * It is useful for removing outdated or no longer required data from the store.
     * </p>
     *
     * @param key The key used to identify the item to be deleted.
     */
    void delete(ItemStack key);

    /**
     * Checks if a specific key exists in the store.
     * <p>
     * This method verifies whether a particular key is present in the store.
     * It is often used to ensure the presence of data before attempting to retrieve or manipulate it.
     * </p>
     *
     * @param key The key to be checked.
     * @return {@code true} if the key exists in the store, {@code false} otherwise.
     */
    boolean containsKey(ItemStack key);
}
