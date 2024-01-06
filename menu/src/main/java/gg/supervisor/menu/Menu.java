package gg.supervisor.menu;

import gg.supervisor.items.Item;
import org.bukkit.inventory.InventoryHolder;

import java.util.Map;

public interface Menu extends InventoryHolder {

    String getId();

    Map<Integer, Item> getItems();

    void redraw();
}
