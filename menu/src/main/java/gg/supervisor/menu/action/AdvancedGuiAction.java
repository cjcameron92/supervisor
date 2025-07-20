package gg.supervisor.menu.action;

import gg.supervisor.menu.item.MenuItem;
import org.bukkit.event.Event;

@FunctionalInterface
public interface AdvancedGuiAction<T extends Event> {

    void run(final T event, MenuItem menuItem);
}
