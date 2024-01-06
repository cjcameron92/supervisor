package gg.supervisor.menu;

import gg.supervisor.items.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;



public class MenuListener implements Listener {

    private final MenuRegistry menuRegistry;

    public MenuListener(MenuRegistry menuRegistry) {
        this.menuRegistry = menuRegistry;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();
        final InventoryHolder holder = inventory.getHolder();
        if (holder != null && holder instanceof Menu menu) {
            event.setCancelled(true);
            if (menuRegistry.getInventories().getIfPresent(menu.getId()) != null) {
                if (menu.getItems().containsKey(event.getSlot())) {
                    final Item item = menu.getItems().get(event.getSlot());
                    if (item.getInventoryClickListener() != null)
                        item.getInventoryClickListener().accept(event);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();
        final InventoryHolder holder = inventory.getHolder();

        if (holder != null && holder instanceof Menu menu) {
            menuRegistry.getInventories().invalidate(menu.getId());
        }
    }
}
