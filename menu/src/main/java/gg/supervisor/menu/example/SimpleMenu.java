package gg.supervisor.menu.example;

import gg.supervisor.menu.builder.ItemBuilder;
import gg.supervisor.menu.entities.InteractionModifier;
import gg.supervisor.menu.guis.impl.PersonalizedMenu;
import gg.supervisor.util.chat.Text;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SimpleMenu extends PersonalizedMenu {

    public SimpleMenu(Player player) {
        super(player, 3, Text.translate("&aSimple"), InteractionModifier.VALUES);
    }

    @Override
    public void redraw() {

        setItem(2, 4, ItemBuilder.from(Material.DIAMOND).name(String.valueOf(System.currentTimeMillis())).menuItem(event -> redraw()));

        setItem(2, 6, ItemBuilder.from(Material.EMERALD).name(String.valueOf(System.currentTimeMillis())).menuItem(event -> {
            val itemStack = getMenuItem(event.getSlot()).getItemStack();

            updateItem(event.getSlot(), ItemBuilder.from(itemStack).name(String.valueOf(System.currentTimeMillis())).editItem(item -> item.setType(Material.ARROW)).build(string -> string.replace("0", "T")));
        }));

    }
}
