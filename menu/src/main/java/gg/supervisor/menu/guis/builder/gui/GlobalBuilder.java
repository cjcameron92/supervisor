package gg.supervisor.menu.guis.builder.gui;

import gg.supervisor.menu.guis.builder.GenericBuilder;
import gg.supervisor.menu.guis.impl.GlobalMenu;
import org.bukkit.entity.Player;

public class GlobalBuilder extends GenericBuilder<GlobalMenu, GlobalBuilder> {

    public GlobalMenu build(Player player) {

        final GlobalMenu menu = new GlobalMenu(getRows(), getTitle(), getModifiers());

        menu.getDecorator().decorate(getSchemaBuilder().build());

        getConsumer().accept(menu);

        return menu;
    }


}
