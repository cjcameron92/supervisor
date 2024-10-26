package gg.supervisor.menu.guis;

import gg.supervisor.menu.exception.MenuException;
import gg.supervisor.menu.guis.item.MenuItem;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class Decorator {

    private final BaseGui gui;

    private final List<String> schema = new ArrayList<>();

    public void decorate(String... lines) {
        schema.clear();
        schema.addAll(List.of(Arrays.copyOfRange(lines, 0, gui.getRows())));
    }

    public void set(char key, MenuItem menuItem) {
        forEach(key, slot -> gui.setItem(slot, menuItem));
    }

    public void set(char key, ItemStack itemStack) {
        set(key, new MenuItem(itemStack));
    }

    public void add(char key, MenuItem menuItem) {
        int firstEmpty = firstEmpty(key);

        if (firstEmpty == -1)
            return;

        gui.setItem(firstEmpty, menuItem);
    }

    public void add(char key, ItemStack itemStack) {
        add(key, new MenuItem(itemStack));
    }

    public void add(char key, List<MenuItem> menuItems) {
        Iterator<MenuItem> iterator = menuItems.iterator();

        forFirstEmpty(key, menuItems.size(), slot -> gui.setItem(slot, iterator.next()));
    }

    public void addItems(char key, List<ItemStack> itemStacks) { // Rename to avoid erasure
        add(key, itemStacks.stream().map(MenuItem::new).toList());
    }

    public void remove(char key) {
        forEach(key, gui::removeItem);
    }

    public void forEach(char key, Consumer<Integer> slotConsumer) {
        if (schema.isEmpty())
            throw new MenuException("You must decorate this first!");

        for (int i = 0; i < schema.size(); i++) {
            String line = schema.get(i);
            char[] chars = line.toCharArray();

            for (int column = 0; column < chars.length; column++) {
                char character = chars[column];

                int slot = column + (i * 9);

                if (slot > gui.getInventory().getSize() - 1)
                    continue;

                if (key == character)
                    slotConsumer.accept(slot);
            }
        }
    }

    public void forFirstEmpty(char key, int amount, Consumer<Integer> slotConsumer) {

        int empties = 0;

        if (schema.isEmpty())
            throw new MenuException("You must decorate this first!");

        for (int i = 0; i < schema.size(); i++) {
            String line = schema.get(i);
            char[] chars = line.toCharArray();

            for (int column = 0; column < chars.length; column++) {
                char character = chars[column];

                if (character == key) continue;

                int slot = column + (i * 9);

                if (++empties > amount || slot > gui.getInventory().getSize() - 1)
                    break;

                if (gui.getMenuItem(slot) != null || gui.getSlotAction(slot) != null)
                    continue;

                slotConsumer.accept(slot);
            }
        }
    }

    /**
     * @param key takes a character
     * @return slot number or -1 if none are empty
     */
    public int firstEmpty(char key) {
        if (schema.isEmpty())
            throw new MenuException("You must decorate this first!");

        for (int i = 0; i < schema.size(); i++) {
            String line = schema.get(i);
            char[] chars = line.toCharArray();

            for (int column = 0; column < chars.length; column++) {
                char character = chars[column];

                if (character == key) continue;

                int slot = column + (i * 9);

                if (slot > gui.getInventory().getSize() - 1)
                    break;

                if (gui.getMenuItem(slot) != null || gui.getSlotAction(slot) != null)
                    continue;

                return slot;
            }
        }

        return -1;
    }

}
