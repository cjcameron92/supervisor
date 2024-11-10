package com.vertmix.supervisor.menu;

import com.vertmix.supervisor.core.bukkit.item.Icon;

import java.util.Collection;

public interface PagedMenu extends Menu {

    void add(Collection<Icon> icons, MenuAction action);

    void next();

    void previous();
}
