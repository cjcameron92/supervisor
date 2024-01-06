package gg.supervisor.menu;


import gg.supervisor.api.Services;
import gg.supervisor.items.Item;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImmutableMenuBuilder implements MenuBuilder {

    private final String id;
    private final Map<Integer, Item> items;

    private String[] shape;

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
                System.out.println("registered listener!");
            }
        }


        // cache
        menuRegistry.getInventories().put(id, menu);
        return menu;
    }

    static class ImmutableMenu implements Menu {

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
}
