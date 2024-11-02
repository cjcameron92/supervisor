package gg.supervisor.menu.guis;

import gg.supervisor.menu.item.MenuItem;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@code Pager} class is responsible for managing pagination of {@link MenuItem} instances within a {@link BaseGui}.
 * It allows for navigating through pages of items, supports endless scrolling, and updates the GUI accordingly.
 */
public class Pager {

    private final @NonNull BaseGui gui;
    private final char decoratorChar;

    private final List<MenuItem> pageItems = new ArrayList<>();
    private @Getter List<MenuItem> currentPageItems = new ArrayList<>();

    private @Getter int step;
    private @Getter EndlessType endless = EndlessType.NONE;

    private int page = 0;

    /**
     * Constructs a new {@code Pager} instance.
     *
     * @param gui           The {@link BaseGui} associated with this pager. Must not be null.
     * @param decoratorChar The character used to decorate the GUI with items.
     */
    public Pager(@NonNull BaseGui gui, char decoratorChar) {
        this.gui = gui;
        this.decoratorChar = decoratorChar;
        this.step = getPageSize();
    }

    /**
     * Gets the current page number.
     *
     * @return The current page number, clamped between 0 and the total number of pages.
     */
    public int getPage() {
        page = Math.max(0, Math.min(page, getTotalPages()));
        return page;
    }

    /**
     * Sets the current page number and updates the displayed items.
     *
     * @param page The page number to set.
     */
    public void setPage(int page) {
        if (this.page != page) {
            this.page = page;
            updatePage();
        }
    }

    /**
     * Sets the number of items to display per page.
     *
     * @param step The number of items per page. Must be at least 1.
     * @return The current {@code Pager} instance for method chaining.
     */
    public Pager step(int step) {
        this.step = Math.max(Math.min(step, getPageSize()), 1);
        return this;
    }

    /**
     * Sets the endless type of the pager.
     *
     * @param endless The type of endless pagination to use.
     * @return The current {@code Pager} instance for method chaining.
     */
    public Pager endless(EndlessType endless) {
        this.endless = endless;
        return this;
    }

    /**
     * Gets the size of the page, determined by the number of slots available for the decorator character.
     *
     * @return The size of the page.
     */
    private int getPageSize() {
        return gui.getDecorator().getSlots(decoratorChar).size();
    }

    /**
     * Calculates the total number of pages available based on the current items and the endless type.
     *
     * @return The total number of pages.
     */
    public int getTotalPages() {
        return endless == EndlessType.TRULY_ENDLESS
                ? pageItems.size()
                : (int) (Math.ceil((double) pageItems.size() / step) - Math.floorDiv(getPageSize(), step));
    }

    /**
     * Checks if there is a next page available.
     *
     * @return {@code true} if there is a next page; {@code false} otherwise.
     */
    public boolean hasNextPage() {
        return endless != EndlessType.NONE || page < getTotalPages();
    }

    /**
     * Checks if there is a previous page available.
     *
     * @return {@code true} if there is a previous page; {@code false} otherwise.
     */
    public boolean hasPreviousPage() {
        return endless != EndlessType.NONE || page > 0;
    }

    /**
     * Updates the current page items based on the current page number and step size.
     * This method refreshes the items displayed in the GUI.
     */
    public void updatePage() {
        if (pageItems.isEmpty()) return;

        int start = (getPage() * getStep()) % pageItems.size();
        int end = Math.min(pageItems.size(), start + getPageSize());

        if (endless != EndlessType.NONE) {
            currentPageItems = new ArrayList<>(pageItems.subList(start, end));

            if (endless == EndlessType.TRULY_ENDLESS && currentPageItems.size() != getPageSize()) {
                int dItem = getPageSize() - currentPageItems.size();
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
     * Adds a single {@link MenuItem} to the pager and updates the page if necessary.
     *
     * @param item The {@link MenuItem} to add.
     */
    public void add(MenuItem item) {
        boolean shouldReload = pageItems.isEmpty() || pageItems.size() < (((getPage() * getStep()) % pageItems.size()) + getPageSize());
        pageItems.add(item);
        if (shouldReload) updatePage();
    }

    /**
     * Adds multiple {@link MenuItem} instances to the pager and updates the page if necessary.
     *
     * @param items The array of {@link MenuItem} to add.
     */
    public void add(MenuItem... items) {
        boolean shouldReload = pageItems.isEmpty() || pageItems.size() < (((getPage() * getStep()) % pageItems.size()) + getPageSize());
        pageItems.addAll(List.of(items));
        if (shouldReload) updatePage();
    }

    /**
     * Adds a list of {@link MenuItem} instances to the pager and updates the page if necessary.
     *
     * @param items The list of {@link MenuItem} to add.
     */
    public void add(List<MenuItem> items) {
        boolean shouldReload = pageItems.isEmpty() || pageItems.size() < (((getPage() * getStep()) % pageItems.size()) + getPageSize());
        pageItems.addAll(items);
        if (shouldReload) updatePage();
    }

    /**
     * Adds a {@link MenuItem} to the pager without triggering an update.
     *
     * @param item The {@link MenuItem} to add.
     */
    public void quietlyAdd(MenuItem item) {
        pageItems.add(item);
    }

    /**
     * Adds multiple {@link MenuItem} instances to the pager without triggering an update.
     *
     * @param items The array of {@link MenuItem} to add.
     */
    public void quietlyAdd(MenuItem... items) {
        pageItems.addAll(List.of(items));
    }

    /**
     * Adds a list of {@link MenuItem} instances to the pager without triggering an update.
     *
     * @param items The list of {@link MenuItem} to add.
     */
    public void quietlyAdd(List<MenuItem> items) {
        pageItems.addAll(items);
    }

    /**
     * Removes a specific {@link MenuItem} from the pager.
     *
     * @param item The {@link MenuItem} to remove.
     */
    public void remove(MenuItem item) {
        removeItemIfMatch(item);
    }

    /**
     * Removes a specific {@link ItemStack} from the pager.
     *
     * @param item The {@link ItemStack} to remove.
     */
    public void remove(ItemStack item) {
        removeItemIfMatch(item);
    }

    /**
     * Clears all items from the pager.
     *
     * @param update If {@code true}, updates the page after clearing the items.
     */
    public void clear(boolean update) {
        pageItems.clear();
        if (update) updatePage();
    }

    /**
     * Advances to the next page if available.
     *
     * @return {@code true} if the next page was successfully navigated to; {@code false} otherwise.
     */
    public boolean next() {
        if (!hasNextPage()) return false;
        page = (page + 1) % (endless == EndlessType.TRULY_ENDLESS ? getTotalPages() : getTotalPages() + 1);
        updatePage();
        return true;
    }

    /**
     * Goes back to the previous page if available.
     *
     * @return {@code true} if the previous page was successfully navigated to; {@code false} otherwise.
     */
    public boolean previous() {
        if (!hasPreviousPage()) return false;

        page = endless != EndlessType.NONE
                ? ((getPage() - 1) < 0
                ? (endless == EndlessType.TRULY_ENDLESS ? getTotalPages() - 1 : getTotalPages())
                : (getPage() - 1))
                : page - 1;

        updatePage();
        return true;
    }

    /**
     * Removes a specific item if it matches the given object.
     *
     * @param item The object to match against the items in the pager.
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
     * The {@code EndlessType} enum defines the different modes of pagination for the pager.
     */
    public enum EndlessType {
        NONE,
        SIMPLE,
        TRULY_ENDLESS
    }
}
