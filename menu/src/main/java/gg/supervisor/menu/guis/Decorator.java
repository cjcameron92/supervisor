package gg.supervisor.menu.guis;

import gg.supervisor.menu.exception.MenuException;
import gg.supervisor.menu.item.MenuItem;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

/**
 * The {@code Decorator} class provides methods to manage and update items in a GUI based on a schema pattern.
 * Allows for decorating a GUI with items mapped to characters in a schema, with support for adding,
 * removing, and setting items in specific slots.
 */
@RequiredArgsConstructor
public class Decorator {

    private final BaseGui gui;
    private final Map<Character, List<Integer>> schemaSlots = new HashMap<>();
    private final List<String> schema = new ArrayList<>();


    /**
     * Initializes the decorator schema with lines representing item layouts for the GUI.
     *
     * @param lines the schema lines defining the layout for GUI items, mapped by characters to GUI slots.
     */
    public void decorate(String... lines) {
        schema.clear();
        schema.addAll(List.of(Arrays.copyOfRange(lines, 0, Math.min(lines.length, gui.getRows()))));

        schemaSlots.clear();
        schemaSlots.putAll(getSchemaSlots());
    }

    /**
     * Sets the specified character to be associated with the given list of slots in the schema.
     *
     * @param character the character key to associate with the slots
     * @param slots the list of slot indices to be associated with the character key
     */
    public void setCharSlot(char character, List<Integer> slots) {
        schemaSlots.put(character, slots);
    }

    /**
     * Sets a {@link MenuItem} for all slots associated with a given character key in the schema.
     *
     * @param key      the schema character that maps to specific GUI slots
     * @param menuItem the {@link MenuItem} to set
     */
    public void set(char key, MenuItem menuItem) {
        forEach(key, slot -> gui.setItem(slot, menuItem));
    }

    /**
     * Sets an {@link ItemStack} for all slots associated with a given character key in the schema.
     *
     * @param key       the schema character that maps to specific GUI slots
     * @param itemStack the {@link ItemStack} to set, wrapped in a {@link MenuItem}
     */
    public void set(char key, ItemStack itemStack) {
        set(key, new MenuItem(itemStack));
    }

    /**
     * Adds a {@link MenuItem} to the first empty slot associated with a given character key in the schema.
     *
     * @param key      the schema character that maps to specific GUI slots
     * @param menuItem the {@link MenuItem} to add
     */
    public void add(char key, MenuItem menuItem) {
        int firstEmpty = firstEmpty(key);
        if (firstEmpty == -1)
            return;

        gui.setItem(firstEmpty, menuItem);
    }

    /**
     * Adds an {@link ItemStack} to the first empty slot associated with a given character key in the schema.
     *
     * @param key       the schema character that maps to specific GUI slots
     * @param itemStack the {@link ItemStack} to add, wrapped in a {@link MenuItem}
     */
    public void add(char key, ItemStack itemStack) {
        add(key, new MenuItem(itemStack));
    }

    /**
     * Adds a list of {@link MenuItem}s to the first available empty slots associated with a given character key.
     *
     * @param key       the schema character that maps to specific GUI slots
     * @param menuItems the list of {@link MenuItem}s to add
     */
    public void add(char key, List<MenuItem> menuItems) {
        Iterator<MenuItem> iterator = menuItems.iterator();
        forFirstEmpty(key, menuItems.size(), slot -> gui.setItem(slot, iterator.next()));
    }

    /**
     * Adds a list of {@link ItemStack}s to the first available empty slots associated with a given character key.
     *
     * @param key        the schema character that maps to specific GUI slots
     * @param itemStacks the list of {@link ItemStack}s to add, each wrapped in a {@link MenuItem}
     */
    public void addItems(char key, List<ItemStack> itemStacks) {
        add(key, itemStacks.stream().map(MenuItem::new).toList());
    }

    /**
     * Removes items from all slots associated with a given character key in the schema.
     *
     * @param key the schema character that maps to specific GUI slots
     */
    public void remove(char key) {
        forEach(key, gui::removeItem);
    }

    /**
     * Applies a specified action to all slots associated with a given character key in the schema.
     *
     * @param key          the schema character that maps to specific GUI slots
     * @param slotConsumer the action to apply to each mapped slot
     * @throws MenuException if no schema has been set
     */
    public void forEach(char key, Consumer<Integer> slotConsumer) {
        if (schema.isEmpty())
            throw new MenuException("You must decorate this first!");

        for (Integer slot : schemaSlots.getOrDefault(key, new ArrayList<>())) {
            if (slot > gui.getInventory().getSize() - 1)
                break;

            slotConsumer.accept(slot);
        }
    }

    /**
     * Applies a specified action to the first available empty slots associated with a given character key in the schema.
     *
     * @param key          the schema character that maps to specific GUI slots
     * @param amount       the number of empty slots to process
     * @param slotConsumer the action to apply to each empty slot
     * @throws MenuException if no schema has been set
     */
    public void forFirstEmpty(char key, int amount, Consumer<Integer> slotConsumer) {
        if (schema.isEmpty())
            throw new MenuException("You must decorate this first!");

        int empties = 0;

        for (Integer slot : schemaSlots.getOrDefault(key, new ArrayList<>())) {
            if (++empties > amount || slot > gui.getInventory().getSize() - 1)
                break;

            if (gui.getMenuItem(slot) != null || gui.getSlotAction(slot) != null)
                continue;

            slotConsumer.accept(slot);
        }
    }

    /**
     * Finds the first empty slot associated with a given character key in the schema.
     *
     * @param key the schema character that maps to specific GUI slots
     * @return the first empty slot index, or -1 if no empty slots are found
     * @throws MenuException if no schema has been set
     */
    public int firstEmpty(char key) {
        if (schema.isEmpty())
            throw new MenuException("You must decorate this first!");

        return schemaSlots.getOrDefault(key, new ArrayList<>()).stream().findFirst().orElse(-1);
    }

    /**
     * Gets all slots associated with a given character key in the schema.
     *
     * @param key the schema character that maps to specific GUI slots
     * @return a list of slot indices for the specified key
     * @throws MenuException if no schema has been set
     */
    public List<Integer> getSlots(char key) {
        if (schema.isEmpty())
            throw new MenuException("You must decorate this first!");

        return schemaSlots.getOrDefault(key, new ArrayList<>());
    }

    /**
     * Processes the schema and returns a mapping of each character key to its corresponding slot indices.
     *
     * @return a map of schema characters to lists of slot indices
     */
    private Map<Character, List<Integer>> getSchemaSlots() {
        Map<Character, List<Integer>> toReturn = new HashMap<>();

        for (int i = 0; i < schema.size(); i++) {
            String line = schema.get(i);
            char[] chars = line.toCharArray();

            for (int column = 0; column < chars.length; column++) {
                char character = chars[column];

                int slot = column + (i * 9);
                if (slot > gui.getInventory().getSize() - 1)
                    break;

                toReturn.putIfAbsent(character, new ArrayList<>());

                toReturn.get(character).add(slot);
            }
        }

        return toReturn;
    }
}
