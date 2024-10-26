package gg.supervisor.menu.guis.listener;

import gg.supervisor.menu.guis.BaseGui;
import gg.supervisor.menu.guis.action.GuiAction;
import gg.supervisor.menu.guis.item.MenuItem;
import gg.supervisor.menu.util.ItemContainers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

public class MenuListener implements Listener {

    @EventHandler
    public void onGuiClick(final InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui gui)) return;

        final GuiAction<InventoryClickEvent> outsideClickAction = gui.getOutsideClickAction();
        if (outsideClickAction != null && event.getClickedInventory() == null) {
            outsideClickAction.run(event);
            return;
        }

        if (event.getClickedInventory() == null) return;

        final GuiAction<InventoryClickEvent> defaultTopClick = gui.getDefaultTopClickAction();
        if (defaultTopClick != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            defaultTopClick.run(event);
        }

        final GuiAction<InventoryClickEvent> playerInventoryClick = gui.getPlayerInventoryAction();
        if (playerInventoryClick != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
            playerInventoryClick.run(event);
        }

        final GuiAction<InventoryClickEvent> defaultClick = gui.getDefaultClickAction();
        if (defaultClick != null) defaultClick.run(event);

        final GuiAction<InventoryClickEvent> slotAction = gui.getSlotAction(event.getSlot());
        if (slotAction != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            slotAction.run(event);
        }

        MenuItem menuItem;

        // Checks whether it's a paginated gui or not
//        if (gui instanceof PaginatedGui) {
//            final PaginatedGui paginatedGui = (PaginatedGui) gui;
//
//            // Gets the gui item from the added items or the page items
//            menuItem = paginatedGui.getMenuItem(event.getSlot());
//            if (menuItem == null) menuItem = paginatedGui.getPageItem(event.getSlot());
//
//        } else {
        // The clicked GUI Item
        menuItem = gui.getMenuItem(event.getSlot());
//        }


        if (!isMenuItem(event.getCurrentItem(), menuItem)) return;

        final GuiAction<InventoryClickEvent> itemAction = menuItem.getAction();
        if (itemAction != null) itemAction.run(event);
    }

    @EventHandler
    public void onGuiDrag(final InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui gui)) return;

        final GuiAction<InventoryDragEvent> dragAction = gui.getDragAction();
        if (dragAction != null) dragAction.run(event);
    }

    @EventHandler
    public void onGuiClose(final InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui gui)) return;

        final GuiAction<InventoryCloseEvent> closeAction = gui.getCloseGuiAction();
        if (closeAction != null && !gui.isUpdating() && gui.isRunCloseAction()) closeAction.run(event);
    }

    @EventHandler
    public void onGuiOpen(final InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui gui)) return;

        if (gui.isUpdating()) return;

        Optional.ofNullable(gui.getOpenGuiAction()).ifPresent(action -> action.run(event));
    }

    private boolean isMenuItem(@Nullable final ItemStack currentItem, @Nullable final MenuItem MenuItem) {
        if (currentItem == null || MenuItem == null)
            return false;

        final String nbt = ItemContainers.getValue(currentItem, "menu-item");
        if (nbt == null)
            return false;

        return nbt.equals(MenuItem.getUuid().toString());
    }
}
