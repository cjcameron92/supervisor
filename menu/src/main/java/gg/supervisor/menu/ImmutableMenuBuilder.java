package gg.supervisor.menu;


import gg.supervisor.api.Services;
import gg.supervisor.api.util.Text;
import gg.supervisor.items.Item;
import gg.supervisor.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ImmutableMenuBuilder implements MenuBuilder {

    private final String id;
    private final Map<Integer, Item> items;
    private String[] shape;

    private int[] safe;
    private boolean pageable = false;
    private int index = 0;

    public ImmutableMenuBuilder() {
        this.id = UUID.randomUUID().toString();
        this.items = new HashMap<>();
    }

    @Override
    public MenuBuilder shape(String... shape) {
        this.shape = shape;
        return this;
    }

    @Override
    public MenuBuilder add(Integer slot, Item item) {
        this.items.put(slot, item);
        return this;
    }

    @Override
    public MenuBuilder add(char index, Item item) {
        if (shape == null) throw new NullPointerException("The menu shape has not been defined");
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length(); j++) {
                if (shape[i].charAt(j) == index) {
                    int slot = (i * 9) + j;
                    add(slot, item);
                }
            }
        }

        return this;
    }

    @Override
    public MenuBuilder pageable(int[] safe, Collection<Item> items) {

        for (Item item : items)
            this.items.put(index++, item);

        this.pageable = true;
        this.safe = safe;
        return this;
    }

    @Override
    public Menu build(Component title, int size) {
        final Menu menu = new ImmutableMenu(id, items, title, size);

        MenuRegistry menuRegistry = Services.loadIfPresent(MenuRegistry.class);
        if (menuRegistry == null) {
            menuRegistry = new MenuRegistry();
            Services.register(MenuRegistry.class, menuRegistry);
        }

        MenuListener menuListener = Services.loadIfPresent(MenuListener.class);
        if (menuListener == null) {
            menuListener = new MenuListener(menuRegistry);
            final Plugin plugin = Services.loadIfPresent(Plugin.class);
            if (plugin != null) {
                Bukkit.getPluginManager().registerEvents(menuListener, plugin);
                Services.register(MenuListener.class, menuListener);
            }
        }


        // cache
        menuRegistry.getInventories().put(id, menu);
        return menu;
    }

    public static class ImmutableMenu implements Menu {

        private final String id;
        private final Map<Integer, Item> items;
        private final Inventory inventory;

        public ImmutableMenu(String id, Map<Integer, Item> items, Component title, int size) {
            this.id = id;
            this.items = items;

            this.inventory = Bukkit.createInventory(this, size, title);
            this.redraw();
        }

        @Override
        public void redraw() {
            if (items.isEmpty()) return;

            items.forEach((slot, item) -> inventory.setItem(slot, item.buildItem(1, $ -> {})));
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public Map<Integer, Item> getItems() {
            return this.items;
        }

        @Override
        public Inventory getInventory() {
            return this.inventory;
        }
    }

    public static class ImmutablePageableMenu extends ImmutableMenu {

        private final int[] safe;
        private final List<Item> contents;

        private final int page, next, back;

        public ImmutablePageableMenu(String id, Map<Integer, Item> items, Component title, int size, int[] safe, List<Item> contents, int page) {
            super(id, items, title, size);
            this.safe = safe;
            this.contents = contents;
            this.page = page;
            this.next = size - 3;
            this.back = size - 7;
        }

        public ImmutablePageableMenu(String id, Map<Integer, Item> items, List<Item> allContents, Component title, int size, int[] safe, int page, int next, int back) {
            super(id, items, title, size);
            this.safe = safe;
            this.contents = allContents;
            this.next = next;
            this.back = back;
            this.page = page;
        }

        @Override
        public void redraw() {
            final int[] box = getSlotsInBox(safe[0], safe[1]);
            if (box.length == 0) throw new RuntimeException("Could not create a box!");

            // Clear the safe area
            for (int slot : box) {
                getInventory().clear(slot);
            }

            // Calculate the start index of the items on the current page
            int startIndex = page * box.length;
            // Calculate the max index, ensuring we do not go out of bounds
            int maxIndex = Math.min(startIndex + box.length, contents.size());

            // Add items from the current page into the safe slots
            for (int i = 0; i < maxIndex - startIndex; i++) {
                if ((startIndex + i) < contents.size()) {
                    Item item = contents.get(startIndex + i);
                    if (item != null) {
                        getInventory().setItem(box[i], item.buildItem(1, $ -> {}));
                        // update cache
                        getItems().put(box[i], item);
                    }
                }
            }

            if (maxIndex < contents.size()) {
                Item nextItem = ItemBuilder.newMenuItem(Material.RED_STAINED_GLASS_PANE).name(Text.translate("&aNext Page")).build();
                getInventory().setItem(next, nextItem.buildItem(1));
                getItems().put(next, nextItem);
            }

            if (page > 0) {
                Item backItem = ItemBuilder.newMenuItem(Material.RED_STAINED_GLASS_PANE).name(Text.translate("&cGo Back")).build();
                getInventory().setItem(back, backItem.buildItem(1));
                getItems().put(back - 6, backItem);
            }

            super.redraw();
        }

        public static int[] getSlotsInBox(int start, int stop) {
            int columns = 9; // Standard columns in a Spigot 1.19 GUI
            // Calculate starting row and column
            int startRow = (start - 1) / columns;
            int startCol = (start - 1) % columns;

            // Calculate ending row and column
            int stopRow = (stop - 1) / columns;
            int stopCol = (stop - 1) % columns;

            ArrayList<Integer> slotList = new ArrayList<>();

            // Loop through rows and columns to add slot numbers
            for (int row = startRow; row <= stopRow; row++) {
                for (int col = startCol; col <= stopCol; col++) {
                    int slotNumber = (row * columns) + col + 1;
                    slotList.add(slotNumber);
                }
            }

            // Convert ArrayList to array
            return slotList.stream().mapToInt(i -> i).toArray();
        }
    }
}
