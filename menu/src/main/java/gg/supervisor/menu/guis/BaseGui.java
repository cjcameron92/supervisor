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

    private @Getter @Setter boolean updating;

    private @Getter @Setter boolean firstRedraw = true;

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

    @NotNull
    public Component title() {
        return title;
    }

    public void open(@NotNull final HumanEntity player) {
        if (player.isSleeping()) return;

        inventory.clear();
        redraw();
        firstRedraw = false;

        player.openInventory(inventory);
    }

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

    public void setItem(final int slot, @NotNull final MenuItem guiItem) {
        validateSlot(slot);

        MenuItems.put(slot, guiItem);
        inventory.setItem(slot, guiItem.getItemStack());
    }

    public void setItem(final int slot, @NotNull final ItemStack itemStack) {
        setItem(slot, new MenuItem(itemStack));
    }

    public void setItem(@NotNull final List<Integer> slots, @NotNull final MenuItem guiItem) {
        for (final int slot : slots) {
            setItem(slot, guiItem);
        }
    }

    public void setItem(final int row, final int col, @NotNull final MenuItem guiItem) {
        setItem(getSlotFromRowCol(row, col), guiItem);
    }

    public void setItem(final int row, final int col, @NotNull final ItemStack itemStack) {
        setItem(row, col, new MenuItem(itemStack));
    }

    public void updateItem(final int slot, @NotNull final ItemStack itemStack) {
        final MenuItem guiItem = MenuItems.get(slot);

        if (guiItem == null) {
            setItem(slot, new MenuItem(itemStack));
            return;
        }

        guiItem.setItemStack(itemStack);
        inventory.setItem(slot, itemStack);
    }

    public void updateItem(final int row, final int col, @NotNull final ItemStack itemStack) {
        updateItem(getSlotFromRowCol(row, col), itemStack);
    }

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

    public void removeItem(final int slot) {
        validateSlot(slot);
        MenuItems.remove(slot);
        inventory.setItem(slot, null);
    }

    public void removeItem(final int row, final int col) {
        removeItem(getSlotFromRowCol(row, col));
    }

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

    public void addItem(@NotNull final MenuItem... items) {
        this.addItem(false, items);
    }

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

    public void fillEmpty(ItemStack itemStack) {
        fillEmpty(new MenuItem(itemStack));
    }

    public void addSlotAction(final int slot, @Nullable final GuiAction<@NotNull InventoryClickEvent> slotAction) {
        validateSlot(slot);
        slotActions.put(slot, slotAction);
    }

    public void addSlotAction(final int row, final int col, @Nullable final GuiAction<@NotNull InventoryClickEvent> slotAction) {
        addSlotAction(getSlotFromRowCol(row, col), slotAction);
    }

    @Nullable
    public GuiAction<InventoryClickEvent> getSlotAction(final int slot) {
        return slotActions.get(slot);
    }


    @Nullable
    public MenuItem getMenuItem(final int slot) {
        return MenuItems.get(slot);
    }

    public void close(@NotNull final HumanEntity player, final boolean runCloseAction, boolean runForwardAction) {
        Runnable task = () -> {
            this.runCloseAction = runCloseAction;
            player.closeInventory();
            this.runCloseAction = true;

            Bukkit.getScheduler().runTaskLater(plugin, () -> { // Run fallback action
                if (runForwardAction && this.fallbackGui != null)
                    this.fallbackGui.apply(player).open(player);
            }, 2L);

        };

        Bukkit.getScheduler().runTaskLater(plugin, task, 2L);
    }

    public void close(@NotNull final HumanEntity player) {
        close(player, true, true);
    }

    public void close(@NotNull final HumanEntity player, final boolean runCloseAction) {
        close(player, runCloseAction, true);
    }

    @NotNull
    private Set<InteractionModifier> safeCopyOf(@NotNull final Set<InteractionModifier> set) {
        if (set.isEmpty()) return EnumSet.noneOf(InteractionModifier.class);
        else return EnumSet.copyOf(set);
    }

    public void updateToViewers() {
        for (HumanEntity viewer : new ArrayList<>(inventory.getViewers())) ((Player) viewer).updateInventory();
    }

    public void update() {
        inventory.clear();

        redraw();

        updateToViewers();
    }

    @Contract(" -> this")
    @NotNull
    public BaseGui disableAllInteractions() {
        interactionModifiers.addAll(InteractionModifier.VALUES);
        return this;
    }

    @Contract(" -> this")
    @NotNull
    public BaseGui addInteractionModifier(@NotNull final InteractionModifier... modifier) {
        interactionModifiers.addAll(List.of(modifier));
        return this;
    }

    public boolean allInteractionsDisabled() {
        return interactionModifiers.size() == InteractionModifier.VALUES.size();
    }

    public boolean canPlaceItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_PLACE);
    }

    public boolean canTakeItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_TAKE);
    }

    public boolean canSwapItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_SWAP);
    }

    public boolean canDropItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_DROP);
    }

    public boolean allowsOtherActions() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_OTHER_ACTIONS);
    }

    private void populateGui() {
        MenuItems.forEach((key, value) -> inventory.setItem(key, value.getItemStack()));
    }

    private int getSlotFromRowCol(final int row, final int col) {
        return 9 * (row - 1) + (col - 1);
    }

    private void validateSlot(final int slot) {
        if (!(slot >= 0 && slot < rows * 9))
            throw new MenuException("The slot " + slot + " is out of range of the GUI's slots");
    }

    public void clearGui() {
        MenuItems.clear();
        inventory.clear();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
