package gg.supervisor.menu.guis.listener;

import com.google.common.base.Preconditions;
import gg.supervisor.menu.guis.BaseGui;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public final class InteractionModifierListener implements Listener {

    private static final Set<InventoryAction> ITEM_TAKE_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ALL, InventoryAction.COLLECT_TO_CURSOR, InventoryAction.HOTBAR_SWAP, InventoryAction.MOVE_TO_OTHER_INVENTORY));
    private static final Set<InventoryAction> ITEM_PLACE_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ALL));
    private static final Set<InventoryAction> ITEM_SWAP_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.HOTBAR_SWAP, InventoryAction.SWAP_WITH_CURSOR, InventoryAction.HOTBAR_MOVE_AND_READD));
    private static final Set<InventoryAction> ITEM_DROP_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.DROP_ONE_SLOT, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_CURSOR, InventoryAction.DROP_ALL_CURSOR));

    @EventHandler
    public void onGuiClick(final InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui gui)) return;

        if (gui.allInteractionsDisabled()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }

        if ((!gui.canPlaceItems() && isPlaceItemEvent(event)) || (!gui.canTakeItems() && isTakeItemEvent(event)) || (!gui.canSwapItems() && isSwapItemEvent(event)) || (!gui.canDropItems() && isDropItemEvent(event)) || (!gui.allowsOtherActions() && isOtherEvent(event))) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onGuiDrag(final InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) return;

        final BaseGui gui = (BaseGui) event.getInventory().getHolder();

        if (gui.allInteractionsDisabled()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }

        if (gui.canPlaceItems() || !isDraggingOnGui(event)) return;

        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
    }

    private boolean isTakeItemEvent(final InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        final Inventory inventory = event.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final InventoryAction action = event.getAction();

        if (clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER || inventory.getType() == InventoryType.PLAYER) {
            return false;
        }

        return action == InventoryAction.MOVE_TO_OTHER_INVENTORY || isTakeAction(action);
    }

    private boolean isPlaceItemEvent(final InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        final Inventory inventory = event.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final InventoryAction action = event.getAction();

        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY
                && clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER
                && inventory.getType() != clickedInventory.getType()) {
            return true;
        }

        return isPlaceAction(action)
                && (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER)
                && inventory.getType() != InventoryType.PLAYER;
    }

    private boolean isSwapItemEvent(final InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        final Inventory inventory = event.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final InventoryAction action = event.getAction();

        return isSwapAction(action)
                && (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER)
                && inventory.getType() != InventoryType.PLAYER;
    }

    private boolean isDropItemEvent(final InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        final Inventory inventory = event.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final InventoryAction action = event.getAction();

        return isDropAction(action)
                && (clickedInventory != null || inventory.getType() != InventoryType.PLAYER);
    }

    private boolean isOtherEvent(final InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        final Inventory inventory = event.getInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final InventoryAction action = event.getAction();

        return isOtherAction(action)
                && (clickedInventory != null || inventory.getType() != InventoryType.PLAYER);
    }

    private boolean isDraggingOnGui(final InventoryDragEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");
        final int topSlots = event.getView().getTopInventory().getSize();
        return event.getRawSlots().stream().anyMatch(slot -> slot < topSlots);
    }

    private boolean isTakeAction(final InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return ITEM_TAKE_ACTIONS.contains(action);
    }

    private boolean isPlaceAction(final InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return ITEM_PLACE_ACTIONS.contains(action);
    }

    private boolean isSwapAction(final InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return ITEM_SWAP_ACTIONS.contains(action);
    }

    private boolean isDropAction(final InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return ITEM_DROP_ACTIONS.contains(action);
    }

    private boolean isOtherAction(final InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return action == InventoryAction.CLONE_STACK || action == InventoryAction.UNKNOWN;
    }
}
