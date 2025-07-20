package gg.supervisor.menu.item;

import com.google.common.base.Preconditions;
import gg.supervisor.menu.action.AdvancedGuiAction;
import gg.supervisor.menu.action.GuiAction;
import gg.supervisor.menu.util.ItemContainers;
import lombok.Getter;
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

    @Getter
    private final UUID uuid = UUID.randomUUID();

    @Getter
    private GuiAction<InventoryClickEvent> action;
    @Getter
    private AdvancedGuiAction<InventoryClickEvent> advancedAction;
    @Getter
    private ItemStack itemStack;

    /**
     * Constructs a MenuItem with a specified ItemStack, a basic action, and an advanced action.
     * Ensures the provided ItemStack is not null.
     *
     * @param itemStack      the ItemStack representing this menu item.
     * @param action         the basic action to be executed when this item is clicked.
     * @param advancedAction the advanced action to be executed when this item is clicked.
     */
    private MenuItem(@NotNull final ItemStack itemStack, @Nullable final GuiAction<@NotNull InventoryClickEvent> action, @Nullable final AdvancedGuiAction<InventoryClickEvent> advancedAction) {
        Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");

        this.action = action;
        this.advancedAction = advancedAction;
        setItemStack(itemStack);
    }

    /**
     * Constructs a MenuItem with a specified ItemStack and no actions.
     *
     * @param itemStack the ItemStack representing this menu item.
     */
    public MenuItem(@NotNull final ItemStack itemStack) {
        this(itemStack, null, null);
    }

    /**
     * Constructs a MenuItem with a specified material and no actions.
     *
     * @param material the material for the ItemStack representing this menu item.
     */
    public MenuItem(@NotNull final Material material) {
        this(new ItemStack(material), null, null);
    }

    /**
     * Constructs a MenuItem with a specified material and a basic action.
     *
     * @param material the material for the ItemStack representing this menu item.
     * @param action   the basic action to be executed when this item is clicked.
     */
    public MenuItem(@NotNull final Material material, @Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        this(new ItemStack(material), action, null);
    }

    /**
     * Constructs a MenuItem with a specified ItemStack and a basic action.
     *
     * @param item   the ItemStack representing this menu item.
     * @param action the basic action to be executed when this item is clicked.
     */
    public MenuItem(@NotNull final ItemStack item, @Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        this(item, action, null);
    }

    /**
     * Constructs a MenuItem with a specified material and an advanced action.
     *
     * @param material the material for the ItemStack representing this menu item.
     * @param action   the advanced action to be executed when this item is clicked.
     */
    public MenuItem(@NotNull final Material material, @Nullable final AdvancedGuiAction<@NotNull InventoryClickEvent> action) {
        this(new ItemStack(material), null, action);
    }

    /**
     * Constructs a MenuItem with a specified ItemStack and an advanced action.
     *
     * @param item   the ItemStack representing this menu item.
     * @param action the advanced action to be executed when this item is clicked.
     */
    public MenuItem(@NotNull final ItemStack item, @Nullable final AdvancedGuiAction<@NotNull InventoryClickEvent> action) {
        this(item, null, action);
    }

    /**
     * Sets an advanced action for this menu item, replacing any existing basic action.
     *
     * @param advancedAction the advanced action to set.
     */
    public void setAction(AdvancedGuiAction<InventoryClickEvent> advancedAction) {
        this.action = null;
        this.advancedAction = advancedAction;
    }

    /**
     * Sets a basic action for this menu item, replacing any existing advanced action.
     *
     * @param action the basic action to set.
     */
    public void setAction(GuiAction<InventoryClickEvent> action) {
        this.advancedAction = null;
        this.action = action;
    }

    /**
     * Sets the ItemStack for this menu item. If the ItemStack type is air, clones it; otherwise, applies metadata.
     *
     * @param itemStack the new ItemStack to set.
     */
    public void setItemStack(@NotNull final ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");

        this.itemStack = itemStack.getType().isAir() ? itemStack.clone() : ItemContainers.applyValue(itemStack, "menu-item", uuid.toString());
    }

    /**
     * Executes the assigned action(s) for this menu item based on the provided inventory click event.
     *
     * @param event the inventory click event triggering this action.
     */
    public void run(InventoryClickEvent event) {
        if (action != null) action.run(event);
        if (advancedAction != null) advancedAction.run(event, this);
    }

    /**
     * Compares this menu item to another object. Two menu items are considered equal if they share the same UUID.
     *
     * @param obj the object to compare against.
     * @return true if the object is equal to this menu item; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MenuItem menuItem)
            return uuid.equals(menuItem.getUuid());

        if (obj instanceof ItemStack item)
            return itemStack.equals(item);

        return itemStack.equals(obj);
    }
}
