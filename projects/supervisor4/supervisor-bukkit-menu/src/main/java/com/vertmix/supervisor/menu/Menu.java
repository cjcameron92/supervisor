package com.vertmix.supervisor.menu;

import com.vertmix.supervisor.core.bukkit.item.Icon;

import java.util.List;
import java.util.Map;

public interface Menu {

    default void render() {}
    default void callback() {}
    void set(char c, Icon icon, MenuAction action);
    List<String> schema();
    Map<String, Object> options();
}
