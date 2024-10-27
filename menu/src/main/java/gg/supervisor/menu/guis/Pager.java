package gg.supervisor.menu.guis;

import gg.supervisor.menu.item.MenuItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class Pager {

    private final BaseGui gui;
    private final char decoratorChar;
    private final @Getter List<MenuItem> pageItems = new ArrayList<>();
    private @Getter List<MenuItem> currentPageItems = new ArrayList<>();

    private @Setter @Getter int step = -1;

//todo implement an optional fillItem
//    private @Setter MenuItem fillItem = new MenuItem(new ItemStack(Material.BARRIER));

    private @Getter int page;

    private int getStepSize() {
        return step == -1 ? getPageSize() : step;
    }

    private int getPageSize() {
        return gui.getDecorator().getSlots(decoratorChar).size();
    }

    public void updatePage() {
        int start = getPage() * getStepSize();
        int end = Math.min(pageItems.size(), start + getPageSize());

        currentPageItems = Collections.unmodifiableList(pageItems.subList(start, end));

        gui.getDecorator().remove(decoratorChar);
        gui.getDecorator().add(decoratorChar, currentPageItems);
    }

    public boolean next() {
        if (!hasNextPage())
            return false;
        page++;

        updatePage();

        return true;
    }

    public boolean previous() {
        if (!hasPreviousPage())
            return false;

        page = Math.max(0, getPage() - 1);

        updatePage();
        return true;
    }

    public boolean hasNextPage() {

        return page < (Math.ceil((double) pageItems.size() / getStepSize()) - 1);
    }

    public boolean hasPreviousPage() {
        return page > 0;
    }

}