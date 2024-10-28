package gg.supervisor.menu.guis;

import gg.supervisor.menu.action.GuiAction;
import gg.supervisor.menu.entities.GuiType;
import gg.supervisor.menu.entities.InteractionModifier;
import gg.supervisor.menu.exception.MenuException;
import gg.supervisor.menu.item.MenuItem;
import gg.supervisor.menu.listener.InteractionModifierListener;
import gg.supervisor.menu.listener.MenuListener;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * BaseGui class represents a basic GUI interface for interacting with items and players.
 */
@SuppressWarnings("unused")
public abstract class BaseGui implements InventoryHolder {

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(BaseGui.class);

    static {
        Bukkit.getPluginManager().registerEvents(new MenuListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new InteractionModifierListener(), plugin);
    }

    private final @Getter Map<Integer, MenuItem> MenuItems;
    private final @Getter Map<Integer, GuiAction<InventoryClickEvent>> slotActions;
    private final @Getter Set<InteractionModifier> interactionModifiers;
    private final @Getter Decorator decorator = new Decorator(this);

    protected @Getter @Setter boolean updating;
    protected @Getter @Setter boolean firstRedraw = true;

    private Inventory inventory;
    private Component title;

    private @Getter int rows = 1;

    private @Getter GuiType guiType = GuiType.CHEST;

    private @Getter @Setter GuiAction<InventoryClickEvent> defaultClickAction;
    private @Getter @Setter GuiAction<InventoryClickEvent> defaultTopClickAction;
    private @Getter @Setter GuiAction<InventoryClickEvent> playerInventoryAction;
    private @Getter @Setter GuiAction<InventoryDragEvent> dragAction;
    private @Getter @Setter GuiAction<InventoryCloseEvent> closeGuiAction;
    private @Getter @Setter GuiAction<InventoryOpenEvent> openGuiAction;
    private @Getter @Setter GuiAction<InventoryClickEvent> outsideClickAction;

    @Nullable
    private @Getter @Setter Function<HumanEntity, BaseGui> fallbackGui = null;

    private @Getter boolean runCloseAction = true;
    private @Getter boolean runOpenAction = true;

    public BaseGui(@NotNull final GuiType guiType, @NotNull final Component title, @NotNull final Set<InteractionModifier> interactionModifiers) {

        this.guiType = guiType;
        this.title = title;
        int inventorySize = guiType.getLimit();

        this.interactionModifiers = safeCopyOf(interactionModifiers);
        this.slotActions = new LinkedHashMap<>(inventorySize);
        this.MenuItems = new LinkedHashMap<>(inventorySize);

        this.inventory = Bukkit.createInventory(this, guiType.getInventoryType(), title);
    }

    public BaseGui(final int rows, @NotNull final Component title, @NotNull final Set<InteractionModifier> interactionModifiers) {
        this.title = title;

        //Ensure rows is within rows >= 1 && rows <= 6
        this.rows = Math.max(1, Math.min(6, rows));
        int inventorySize = this.rows * 9;

        this.interactionModifiers = safeCopyOf(interactionModifiers);
        this.slotActions = new LinkedHashMap<>(inventorySize);
        this.MenuItems = new LinkedHashMap<>(inventorySize);

        this.inventory = Bukkit.createInventory(this, inventorySize, title);
    }

    public void redraw() {
    }

    /**
     * Performs specific actions when the menu is being closed.
     * Invoke this when you need to handle a menu closing, not when the user initiates a 'Close' command.
     * This is a separate process from the CloseAction command, which can be ignored.
     */
    public void onClose() {

    }

    /**
     * Retrieves the title component.
     *
     * @return The title component.
     */
    @NotNull
    public Component title() {
        return title;
    }

    /**
     * Opens a GUI for the specified player.
     *
     * @param player the HumanEntity for whom the GUI will be opened
     */
    protected void open(@NotNull final HumanEntity player) {
        if (player.isSleeping()) return;

        inventory.clear();
        populateGui();
        redraw();
        firstRedraw = false;

        player.openInventory(inventory);
    }

    /**
     * Updates the title of the GUI with the specified component.
     *
     * @param title the new title component to set for the GUI
     * @return this BaseGui instance after updating the title
     */
    @Contract("_ -> this")
    @NotNull
    public BaseGui updateTitle(@NotNull final Component title) {
        updating = true;

        final List<HumanEntity> viewers = new ArrayList<>(inventory.getViewers());

        inventory = Bukkit.createInventory(this, inventory.getSize(), title);

        for (final HumanEntity player : viewers) {
            open(player);
        }

        updating = false;
        this.title = title;
        return this;
    }

    /**
     * Sets the MenuItem at the specified slot in the GUI.
     *
     * @param slot    The slot index where the MenuItem will be set
     * @param guiItem The MenuItem object to be set at the specified slot
     */
    public void setItem(final int slot, @NotNull final MenuItem guiItem) {
        validateSlot(slot);

        MenuItems.put(slot, guiItem);
        inventory.setItem(slot, guiItem.getItemStack());
    }

    /**
     * Sets an item at the specified slot in the GUI with the given ItemStack.
     *
     * @param slot      The slot to set the item at
     * @param itemStack The ItemStack to set as the item
     */
    public void setItem(final int slot, @NotNull final ItemStack itemStack) {
        setItem(slot, new MenuItem(itemStack));
    }

    /**
     * Sets the specified {@link MenuItem} on multiple slots in the GUI.
     *
     * @param slots   The list of slots where the item should be set
     * @param guiItem The {@link MenuItem} to set
     */
    public void setItem(@NotNull final List<Integer> slots, @NotNull final MenuItem guiItem) {
        for (final int slot : slots) {
            setItem(slot, guiItem);
        }
    }

    /**
     * Set the GUI item at the specified row and column.
     *
     * @param row     The row index of the GUI grid
     * @param col     The column index of the GUI grid
     * @param guiItem The MenuItem to set at the specified row and column
     */
    public void setItem(final int row, final int col, @NotNull final MenuItem guiItem) {
        setItem(getSlotFromRowCol(row, col), guiItem);
    }

    /**
     * Sets a MenuItem at the specified row and column in the GUI.
     *
     * @param row       The row index of the item to set
     * @param col       The column index of the item to set
     * @param itemStack The ItemStack to set as a MenuItem at the specified row and column
     */
    public void setItem(final int row, final int col, @NotNull final ItemStack itemStack) {
        setItem(row, col, new MenuItem(itemStack));
    }

    /**
     * Updates the item in the specified slot with the provided ItemStack.
     *
     * @param slot      The slot to update the item in
     * @param itemStack The ItemStack to set as the new item in the slot
     */
    public void updateItem(final int slot, @NotNull final ItemStack itemStack) {
        final MenuItem guiItem = MenuItems.get(slot);

        if (guiItem == null) {
            setItem(slot, new MenuItem(itemStack));
            return;
        }

        guiItem.setItemStack(itemStack);
        inventory.setItem(slot, itemStack);
    }

    /**
     * Updates the item at the specified row and column with the given ItemStack.
     *
     * @param row       The row of the item to update
     * @param col       The column of the item to update
     * @param itemStack The ItemStack to set for the item
     */
    public void updateItem(final int row, final int col, @NotNull final ItemStack itemStack) {
        updateItem(getSlotFromRowCol(row, col), itemStack);
    }

    /**
     * Removes the specified MenuItem from the MenuItems map and its associated inventory.
     *
     * @param item The MenuItem to be removed
     */
    public void removeItem(@NotNull final MenuItem item) {
        final Optional<Map.Entry<Integer, MenuItem>> entry = MenuItems.entrySet()
                .stream()
                .filter(it -> it.getValue().equals(item))
                .findFirst();

        entry.ifPresent(it -> {
            MenuItems.remove(it.getKey());
            inventory.remove(it.getValue().getItemStack());
        });
    }

    /**
     * Removes the specified item from the menu.
     *
     * @param item The ItemStack to be removed from the menu
     */
    public void removeItem(@NotNull final ItemStack item) {
        final Optional<Map.Entry<Integer, MenuItem>> entry = MenuItems.entrySet()
                .stream()
                .filter(it -> it.getValue().getItemStack().equals(item))
                .findFirst();

        entry.ifPresent(it -> {
            MenuItems.remove(it.getKey());
            inventory.remove(item);
        });
    }

    /**
     * Removes an item from a specific slot in the inventory.
     *
     * @param slot the slot number to remove the item from
     */
    public void removeItem(final int slot) {
        validateSlot(slot);
        MenuItems.remove(slot);
        inventory.setItem(slot, null);
    }

    /**
     * Removes an item from the specified row and column location in the GUI inventory.
     *
     * @param row The row index of the item to be removed.
     * @param col The column index of the item to be removed.
     */
    public void removeItem(final int row, final int col) {
        removeItem(getSlotFromRowCol(row, col));
    }

    /**
     * Add one or multiple menu items to the inventory. If the inventory is full and expandIfFull is true, the inventory rows will be increased by one.
     * If the inventory is not expandable, the method will return without adding items.
     *
     * @param expandIfFull Boolean flag indicating whether to expand the inventory if it's full.
     * @param items        The menu items to add to the inventory.
     */
    public void addItem(final boolean expandIfFull, @NotNull final MenuItem... items) {
        final List<MenuItem> notAddedItems = new ArrayList<>();

        for (final MenuItem guiItem : items) {
            for (int slot = 0; slot < rows * 9; slot++) {
                if (MenuItems.get(slot) != null) {
                    if (slot == rows * 9 - 1) {
                        notAddedItems.add(guiItem);
                    }
                    continue;
                }

                MenuItems.put(slot, guiItem);
                break;
            }
        }

        if (!expandIfFull || this.rows >= 6 ||
                notAddedItems.isEmpty() ||
                (this.guiType != null && this.guiType != GuiType.CHEST)) {
            return;
        }

        this.rows++;
        this.inventory = Bukkit.createInventory(this, this.rows * 9, this.title);
        this.update();
        this.addItem(true, notAddedItems.toArray(new MenuItem[0]));
    }

    /**
     * Adds one or more menu items to the GUI.
     *
     * @param items The menu items to be added to the GUI
     */
    public void addItem(@NotNull final MenuItem... items) {
        this.addItem(false, items);
    }

    /**
     * Fills empty slots in the inventory with the provided MenuItem.
     * It will check each slot in the inventory, and if the slot is empty (both MenuItem and GuiAction are null), it will fill it with the provided MenuItem.
     * It stops filling when all empty slots are filled or when all slots have been checked.
     *
     * @param menuItem The MenuItem to fill the empty slots with
     */
    public void fillEmpty(MenuItem menuItem) {
        int count = getMenuItems().size();
        int size = this.inventory.getSize() - 1;

        if (count >= size) return;

        for (int slot = 0; slot < size + 1; ++slot) {

            if (getMenuItem(slot) != null || getSlotAction(slot) != null)
                continue;

            setItem(slot, menuItem);
        }
    }

    /**
     * Fills empty slots in the inventory with the provided MenuItem.
     *
     * @param itemStack The MenuItem to fill empty slots with
     */
    public void fillEmpty(ItemStack itemStack) {
        fillEmpty(new MenuItem(itemStack));
    }

    /**
     * Adds a slot action to handle a specific slot in the GUI.
     *
     * @param slot       The slot index to add the action for.
     * @param slotAction The action to be performed when interacting with the specified slot.
     */
    public void addSlotAction(final int slot, @Nullable final GuiAction<@NotNull InventoryClickEvent> slotAction) {
        validateSlot(slot);
        slotActions.put(slot, slotAction);
    }

    /**
     *
     */
    public void addSlotAction(final int row, final int col, @Nullable final GuiAction<@NotNull InventoryClickEvent> slotAction) {
        addSlotAction(getSlotFromRowCol(row, col), slotAction);
    }

    /**
     * Retrieves the action associated with a specific slot in the GUI.
     *
     * @param slot The slot index for which to retrieve the action.
     * @return The GuiAction object associated with the slot, or null if no action is defined.
     */
    @Nullable
    public GuiAction<InventoryClickEvent> getSlotAction(final int slot) {
        return slotActions.get(slot);
    }


    /**
     * Returns the MenuItem associated with the specified slot in the menu.
     *
     * @param slot the slot index to retrieve the MenuItem from
     * @return the MenuItem at the specified slot, or null if no MenuItem is found
     */
    @Nullable
    public MenuItem getMenuItem(final int slot) {
        return MenuItems.get(slot);
    }

    /**
     * Closes the inventory for the specified player and optionally runs close and forward actions.
     *
     * @param player           the player whose inventory should be closed
     * @param runCloseAction   whether to run the close action before closing the inventory
     * @param runForwardAction whether to run the forward action after closing the inventory
     */
    public void close(@NotNull final HumanEntity player, final boolean runCloseAction, boolean runForwardAction) {

        this.runCloseAction = runCloseAction;
        player.closeInventory();

        this.runCloseAction = true;

        if (!runForwardAction || this.fallbackGui == null)
            return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> { // Run fallback action
            this.fallbackGui.apply(player).open(player);
        }, 2L);

    }

    /**
     * Closes the GUI for the specified player.
     *
     * @param player         The HumanEntity representing the player
     * @param runCloseAction Whether to run the close action or not
     */
    public void close(@NotNull final HumanEntity player, final boolean runCloseAction) {
        close(player, runCloseAction, true);
    }

    /**
     * Closes the GUI for the specified player, allowing to run optional close and forward actions.
     *
     * @param player the HumanEntity for whom the GUI will be closed
     */
    public void close(@NotNull final HumanEntity player) {
        close(player, true, true);
    }


    /**
     * Creates a safe copy of the input set of InteractionModifiers.
     *
     * @param set the set of InteractionModifiers to create a safe copy of
     * @return a new set containing all elements from the input set, or an empty set if the input set is empty
     */
    @NotNull
    private Set<InteractionModifier> safeCopyOf(@NotNull final Set<InteractionModifier> set) {
        if (set.isEmpty()) return EnumSet.noneOf(InteractionModifier.class);
        else return EnumSet.copyOf(set);
    }

    /**
     * Updates the inventory to all viewers by calling updateInventory() on each player viewer.
     */
    public void updateToViewers() {
        for (HumanEntity viewer : new ArrayList<>(inventory.getViewers())) ((Player) viewer).updateInventory();
    }

    /**
     * Clears the inventory, redraws the GUI, and updates the changes to all viewers.
     */
    public void update() {
        inventory.clear();

        redraw();

        updateToViewers();
    }

    /**
     * Disables all interactions for the BaseGui by adding all InteractionModifiers to the existing set.
     *
     * @return The BaseGui instance with all interactions disabled
     */
    @Contract(" -> this")
    @NotNull
    public BaseGui disableAllInteractions() {
        interactionModifiers.addAll(InteractionModifier.VALUES);
        return this;
    }

    /**
     * Adds the given interaction modifiers to the BaseGui.
     *
     * @param modifier The InteractionModifiers to add
     * @return The updated BaseGui instance with the new interaction modifiers added
     */
    @NotNull
    public BaseGui addInteractionModifier(@NotNull final InteractionModifier... modifier) {
        interactionModifiers.addAll(List.of(modifier));
        return this;
    }

    /**
     * Checks if all interactions are disabled in the BaseGui instance.
     *
     * @return true if all interactions are disabled, false otherwise
     */
    public boolean allInteractionsDisabled() {
        return interactionModifiers.size() == InteractionModifier.VALUES.size();
    }

    /**
     * Check if the BaseGui allows placing items based on the interaction modifiers.
     *
     * @return true if item placement is allowed, false otherwise
     */
    public boolean canPlaceItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_PLACE);
    }

    /**
     * Check if the GUI allows taking items based on its interaction modifiers.
     *
     * @return true if the GUI allows taking items, false otherwise
     */
    public boolean canTakeItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_TAKE);
    }

    /**
     * Checks if the item swap interaction is allowed based on the presence of {@link InteractionModifier#PREVENT_ITEM_SWAP}.
     *
     * @return true if item swap interaction is allowed, false if item swap is prevented
     */
    public boolean canSwapItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_SWAP);
    }

    /**
     * Check if the BaseGui allows dropping items.
     *
     * @return true if the BaseGui does not prevent item drop, false otherwise
     */
    public boolean canDropItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_DROP);
    }

    /**
     * Check if the current BaseGui instance allows other actions besides those specified in the InteractionModifier enum.
     *
     * @return true if other actions are allowed, false if other actions are prevented
     */
    public boolean allowsOtherActions() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_OTHER_ACTIONS);
    }

    protected void populateGui() {
        MenuItems.forEach((key, value) -> inventory.setItem(key, value.getItemStack()));
    }

    /**
     * Returns the slot number corresponding to the given row and column in a grid.
     *
     * @param row the row number, starting from 1
     * @param col the column number, starting from 1
     * @return the slot number calculated based on row and column
     */
    private int getSlotFromRowCol(final int row, final int col) {
        return 9 * (row - 1) + (col - 1);
    }

    /**
     * Validates the given slot to ensure it falls within the range of the GUI's slots.
     *
     * @param slot The slot index to validate
     * @throws MenuException if the slot is out of range of the GUI's slots
     */
    private void validateSlot(final int slot) {
        if (!(slot >= 0 && slot < rows * 9))
            throw new MenuException("The slot " + slot + " is out of range of the GUI's slots");
    }

    /**
     * Clears the GUI by removing all menu items and clearing the inventory.
     */
    public void clearGui() {
        MenuItems.clear();
        inventory.clear();
    }

    /**
     * Retrieves the Inventory object associated with this BaseGui.
     *
     * @return The Inventory object representing the contents of the GUI.
     */
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
