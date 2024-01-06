package gg.supervisor.menu;

import gg.supervisor.items.Item;
import net.kyori.adventure.text.Component;

public interface MenuBuilder {

    Menu build(Component title, int size);

    MenuBuilder shape(String... shape);

    MenuBuilder add(Integer slot, Item item);

    MenuBuilder add(char index, Item item);

    static MenuBuilder newBuilder() {
        return new ImmutableMenuBuilder();
    }

}
