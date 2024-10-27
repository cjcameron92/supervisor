package gg.supervisor.menu.guis;

import gg.supervisor.menu.item.MenuItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pager {

    private final @NonNull BaseGui gui;

    private final char decoratorChar;

    private final @Getter List<MenuItem> pageItems = new ArrayList<>();
    private @Getter List<MenuItem> currentPageItems = new ArrayList<>();

    private @Getter int step;
    private @Getter @Setter EndlessType endless = EndlessType.NONE;

    private @Getter int page = 0;

    public Pager(@NonNull BaseGui gui, char decoratorChar) {
        this.gui = gui;
        this.decoratorChar = decoratorChar;

        this.step = getPageSize();
    }

//todo implement an optional fillItem
//    private @Setter MenuItem fillItem = new MenuItem(new ItemStack(Material.BARRIER));

    private int getPageSize() {
        return gui.getDecorator().getSlots(decoratorChar).size();
    }

    public void setStep(int step) {
        this.step = Math.max(Math.min(step, getPageSize()), 1);
    }

    public int getTotalPages() {
        return (int) (Math.ceil((double) pageItems.size() / step) - (endless == EndlessType.TRULY_ENDLESS ? getPageSize() : Math.floorDiv(getPageSize(), step)));
    }

    public boolean hasNextPage() {
        return endless != EndlessType.NONE || page < getTotalPages();
    }

    public boolean hasPreviousPage() {
        return endless != EndlessType.NONE || page > 0;
    }

    public void updatePage() {

        if (pageItems.isEmpty()) // Avoid ArithmeticException: / by zero
            return;

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


    //todo implement adders and removers, along side a clear command

    public boolean next() {
        if (!hasNextPage())
            return false;

        page = (page + 1) % (endless == EndlessType.TRULY_ENDLESS ? pageItems.size() : getTotalPages() + 1);
        updatePage();

        return true;
    }

    public boolean previous() {
        if (!hasPreviousPage())
            return false;

        page = endless != EndlessType.NONE ? ((getPage() - 1) < 0 ? (endless == EndlessType.TRULY_ENDLESS ? pageItems.size() - 1 : getTotalPages()) : (getPage() - 1)) : Math.max(0, getPage() - 1);
        updatePage();

        return true;
    }

    public enum EndlessType {
        NONE,
        SIMPLE,
        TRULY_ENDLESS
    }

}