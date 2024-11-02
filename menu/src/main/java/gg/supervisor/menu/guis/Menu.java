package gg.supervisor.menu.guis;


import gg.supervisor.menu.guis.builder.gui.GlobalBuilder;
import gg.supervisor.menu.guis.builder.gui.PersonalizedBuilder;

public class Menu {

    public static GlobalBuilder global() {
        return new GlobalBuilder();
    }

    public static PersonalizedBuilder personal() {
        return new PersonalizedBuilder();
    }

}
