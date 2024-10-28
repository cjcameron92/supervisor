package gg.supervisor.menu.guis;

import gg.supervisor.menu.item.MenuItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Pager class is responsible for managing pagination of items within a GUI.
 * It supports different pagination modes, such as endless scrolling and fixed pages.
 */
public class Pager {

    private final @NonNull BaseGui gui;
    private final char decoratorChar;

    @Getter private final List<MenuItem> pageItems = new ArrayList<>();
    @Getter private List<MenuItem> currentPageItems = new ArrayList<>();
    @Getter private int step;
    @Getter @Setter private EndlessType endless = EndlessType.NONE;

    private int page = 0;

    /**
     * Constructs a Pager with a specific GUI and decorator character.
     *
     * @param gui           the GUI to associate with this pager
     * @param decoratorChar the character used for decoration in the GUI
     */
    public Pager(@NonNull BaseGui gui, char decoratorChar) {
        this.gui = gui;
        this.decoratorChar = decoratorChar;
        this.step = getPageSize();
    }

    /**
     * Gets the current page index, ensuring it is within valid bounds.
     *
     * @return the current page index
     */
    public int getPage() {
        page = Math.max(0, Math.min(page, getTotalPages()));
        return page;
    }

    public void setPage(int page) {
        if (this.page != page) {
            this.page = page;
            updatePage();
        }
    }

    /**
     * Retrieves the page size based on the decorator character in the GUI.
     *
     * @return the number of slots allocated for items on each page
     */
    private int getPageSize() {
        return gui.getDecorator().getSlots(decoratorChar).size();
    }

    /**
     * Sets the number of items to display per step (page) with a minimum of 1.
     *
     * @param step the number of items per step
     */
    public void setStep(int step) {
        this.step = Math.max(Math.min(step, getPageSize()), 1);
    }

    /**
     * Calculates the total number of pages based on the items and step size.
     *
     * @return the total number of pages
     */
    public int getTotalPages() {
        return endless == EndlessType.TRULY_ENDLESS
                ? pageItems.size()
                : (int) (Math.ceil((double) pageItems.size() / step) - Math.floorDiv(getPageSize(), step));
    }

    /**
     * Determines if there is a next page available based on the current page and pagination type.
     *
     * @return true if there is a next page, otherwise false
     */
    public boolean hasNextPage() {
        return endless != EndlessType.NONE || page < getTotalPages();
    }

    /**
     * Determines if there is a previous page available based on the current page and pagination type.
     *
     * @return true if there is a previous page, otherwise false
     */
    public boolean hasPreviousPage() {
        return endless != EndlessType.NONE || page > 0;
    }

    /**
     * Updates the items on the current page based on the page index and step size.
     */
    public void updatePage() {

        if (pageItems.isEmpty()) return; // Avoid ArithmeticException: / by zero

        int start = (getPage() * getStep()) % pageItems.size();
        int end = Math.min(pageItems.size(), start + getPageSize());

//        System.out.println("rendering: " + page);

        if (endless != EndlessType.NONE) {
            currentPageItems = new ArrayList<>(pageItems.subList(start, end));

            if (endless == EndlessType.TRULY_ENDLESS && currentPageItems.size() != getPageSize()) {
                int dItem = getPageSize() - currentPageItems.size();
//                System.out.println("total Items: " + pageItems.size() + " currentPageItems: " + currentPageItems.size() + " delta:" + dItem);

                currentPageItems.addAll(pageItems.subList(0, dItem));
            }
            currentPageItems = Collections.unmodifiableList(currentPageItems);
        } else {
            currentPageItems = Collections.unmodifiableList(pageItems.subList(start, end));
        }

        gui.getDecorator().remove(decoratorChar);
        gui.getDecorator().add(decoratorChar, currentPageItems);
    }

    /**
     * Adds a single item to the pager and optionally reloads the page if needed.
     *
     * @param item the MenuItem to add
     */
    public void add(MenuItem item) {
        boolean shouldReload = pageItems.isEmpty() || pageItems.size() < (((getPage() * getStep()) % pageItems.size()) + getPageSize());
        pageItems.add(item);
        if (shouldReload) updatePage();
    }

    /**
     * Adds multiple items to the pager and optionally reloads the page if needed.
     *
     * @param items an array of MenuItems to add
     */
    public void add(MenuItem... items) {
        boolean shouldReload = pageItems.isEmpty() || pageItems.size() < (((getPage() * getStep()) % pageItems.size()) + getPageSize());
        pageItems.addAll(List.of(items));
        if (shouldReload) updatePage();
    }

    /**
     * Adds a list of items to the pager and optionally reloads the page if needed.
     *
     * @param items a list of MenuItems to add
     */
    public void add(List<MenuItem> items) {
        boolean shouldReload = pageItems.isEmpty() || pageItems.size() < (((getPage() * getStep()) % pageItems.size()) + getPageSize());
        pageItems.addAll(items);
        if (shouldReload) updatePage();
    }

    /**
     * Adds an item to the pager without reloading the current page.
     *
     * @param item the MenuItem to add
     */
    public void quietlyAdd(MenuItem item) {
        pageItems.add(item);
    }

    /**
     * Adds multiple items to the pager without reloading the current page.
     *
     * @param items an array of MenuItems to add
     */
    public void quietlyAdd(MenuItem... items) {
        pageItems.addAll(List.of(items));
    }

    /**
     * Adds a list of items to the pager without reloading the current page.
     *
     * @param items a list of MenuItems to add
     */
    public void quietlyAdd(List<MenuItem> items) {
        pageItems.addAll(items);
    }

    /**
     * Removes a specified item from the pager.
     *
     * @param item the MenuItem to remove
     */
    public void remove(MenuItem item) {
        removeItemIfMatch(item);
    }

    /**
     * Removes a specified item from the pager by its ItemStack.
     *
     * @param item the ItemStack to remove
     */
    public void remove(ItemStack item) {
        removeItemIfMatch(item);
    }

    /**
     * Clears all items in the pager and optionally updates the current page.
     *
     * @param update whether to update the current page after clearing items
     */
    public void clear(boolean update) {
        pageItems.clear();
        if (update) updatePage();
    }

    /**
     * Advances to the next page if one exists.
     *
     * @return true if the page was advanced, otherwise false
     */
    public boolean next() {
        if (!hasNextPage()) return false;
        page = (page + 1) % (endless == EndlessType.TRULY_ENDLESS ? getTotalPages() : getTotalPages() + 1);
        updatePage();
        return true;
    }

    /**
     * Moves to the previous page if one exists.
     *
     * @return true if the page was moved back, otherwise false
     */
    public boolean previous() {
        if (!hasPreviousPage()) return false;
        page = endless != EndlessType.NONE ? ((getPage() - 1) < 0 ? (endless == EndlessType.TRULY_ENDLESS ? getTotalPages() - 1 : getTotalPages()) : (getPage() - 1)) : page - 1;
        updatePage();
        return true;
    }

    /**
     * Removes an item if it matches the specified item or ItemStack.
     *
     * @param item the item or ItemStack to match and remove
     */
    private void removeItemIfMatch(Object item) {
        final int end = (page + 1) * getPageSize();
        for (int i = pageItems.size() - 1; i >= 0; i--) {
            final MenuItem current = pageItems.get(i);
            if (!current.equals(item)) continue;
            pageItems.remove(i);
            if (i <= end) updatePage();
            return;
        }
    }

    /**
     * Represents the different types of endless pagination modes.
     */
    public enum EndlessType {
        NONE,
        SIMPLE,
        TRULY_ENDLESS
    }
}
