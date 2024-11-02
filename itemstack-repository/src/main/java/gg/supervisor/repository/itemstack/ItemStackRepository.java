package gg.supervisor.repository.itemstack;

import gg.supervisor.core.repository.Repository;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemStackRepository<T> extends Repository<T> {


    /**
     * Finds and returns an entity by its unique key.
     *
     * @param key The unique identifier of the entity to be retrieved. Must not be null.
     * @return The entity associated with the specified key. Never null.
     * @throws IllegalArgumentException if the key is not found in the repository.
     */
    @NotNull
    T find(@NotNull ItemStack key);

    /**
     * Saves or updates an entity in the repository with a specified key.
     *
     * @param key  The unique identifier for the entity to be saved. Must not be null.
     * @param type The entity to be saved in the repository. Must not be null.
     */
    void save(@NotNull ItemStack key, @NotNull T type);

    /**
     * Deletes an entity from the repository by its unique key.
     *
     * @param key The unique identifier of the entity to be deleted. Must not be null.
     * @return {@code true} if the entity was successfully deleted; {@code false} if the key does not exist.
     */
    boolean delete(@NotNull ItemStack key);

    /**
     * Checks if an entity with the specified key exists in the repository.
     *
     * @param key The unique identifier of the entity to check. Must not be null.
     * @return {@code true} if an entity with the given key exists; {@code false} otherwise.
     */
    boolean containsKey(@NotNull ItemStack key);

}
