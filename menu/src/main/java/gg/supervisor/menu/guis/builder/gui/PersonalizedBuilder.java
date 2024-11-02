package gg.supervisor.menu.guis.builder.gui;

import gg.supervisor.menu.guis.builder.GenericBuilder;
import gg.supervisor.menu.guis.impl.PersonalizedMenu;
import org.bukkit.entity.Player;

public class PersonalizedBuilder extends GenericBuilder<PersonalizedMenu, PersonalizedBuilder> {

    public PersonalizedMenu build(Player player) {

        final PersonalizedMenu menu = new PersonalizedMenu(player, getRows(), getTitle(), getModifiers());

        menu.getDecorator().decorate(getSchemaBuilder().build());

        getConsumer().accept(menu);

        return menu;
    }

}
