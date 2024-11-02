package gg.supervisor.menu.item;

import com.google.common.base.Preconditions;
import gg.supervisor.menu.action.GuiAction;
import gg.supervisor.menu.util.ItemContainers;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a single menu item with a unique identifier, an associated action, and an ItemStack.
 */
@SuppressWarnings("unused")
public class MenuItem {

    /**
     * Represents a universally unique identifier (UUID) for the MenuItem.
     * The UUID is generated using UUID.randomUUID() method upon creation of the MenuItem.
     */
    @Getter
    private final UUID uuid = UUID.randomUUID();

    /**
     * Represents a graphical user interface action associated with an InventoryClickEvent.
     * This variable is used to define a custom action performed when an InventoryClickEvent is triggered.
     * It is a getter and setter for a GuiAction instance that processes InventoryClickEvent.
     */
    @Getter
    @Setter
    private GuiAction<InventoryClickEvent> action;

    /**
     * Represents the ItemStack associated with a menu item.
     */
    @Getter
    private ItemStack itemStack;

    /**
     * Constructs a MenuItem with the provided ItemStack and GuiAction.
     *
     * @param itemStack The ItemStack for the GUI Item. Must not be null.
     * @param action The GuiAction associated with the MenuItem.
     */
    public MenuItem(@NotNull final ItemStack itemStack, @Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");

        this.action = action;
        setItemStack(itemStack);
    }

    /**
     * Constructs a MenuItem with the given ItemStack.
     *
     * @param itemStack the ItemStack for the MenuItem
     */
    public MenuItem(@NotNull final ItemStack itemStack) {
        this(itemStack, null);
    }

    /**
     * Constructs a MenuItem instance with the specified Material.
     *
     * @param material the Material to create the ItemStack for this MenuItem
     */
    public MenuItem(@NotNull final Material material) {
        this(new ItemStack(material), null);
    }

    /**
     * Constructs a MenuItem with the specified Material and optional GuiAction.
     *
     * @param material the Material of the ItemStack for this MenuItem. Must not be null.
     * @param action   the GuiAction associated with clicking on this MenuItem. Nullable.
     */
    public MenuItem(@NotNull final Material material, @Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        this(new ItemStack(material), action);
    }

    /**
     * Sets the ItemStack for the GUI Item represented by this MenuItem instance.
     *
     * @param itemStack The ItemStack to be set for the GUI Item
     * @throws NullPointerException if the itemStack parameter is null
     */
    public void setItemStack(@NotNull final ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");

        this.itemStack = itemStack.getType().isAir() ? itemStack.clone() : ItemContainers.applyValue(itemStack, "menu-item", uuid.toString());

    }

    /**
     * Compares this MenuItem with the specified Object for equality.
     *
     * @param obj the Object to compare for equality
     * @return true if the Object is a MenuItem and has the same ItemStack as this MenuItem, or if the Object is an ItemStack and is equal to the ItemStack of this MenuItem, false
     *  otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MenuItem menuItem)
            return itemStack.equals(menuItem.getItemStack());

        if (obj instanceof ItemStack item)
            return itemStack.equals(item);

        return itemStack.equals(obj);
    }
}
