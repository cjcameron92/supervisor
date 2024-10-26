package gg.supervisor.menu.guis.action;

import org.bukkit.event.Event;

@FunctionalInterface
public interface GuiAction<T extends Event> {

    void run(final T event);
}
