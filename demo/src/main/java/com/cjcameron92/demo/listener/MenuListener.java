package com.cjcameron92.demo.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import gg.supervisor.api.Component;
import gg.supervisor.items.Item;
import gg.supervisor.items.ItemBuilder;
import gg.supervisor.menu.MenuBuilder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

@Component
public class MenuListener implements Listener {

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        final Player player = event.getPlayer();

        MenuBuilder menuBuilder = MenuBuilder.newBuilder().add(4, ItemBuilder.newMenuItem(Material.EGG).addInventoryClickListener(e -> e.getWhoClicked().sendMessage("lol")).build());
        player.openInventory(menuBuilder.build(LegacyComponentSerializer.legacyAmpersand().deserialize("demo"), 9).getInventory());

        final Item item = ItemBuilder.newBuilder(Material.STICK, "demo").addDropListener(e -> System.out.println("e")).build();
        final ItemStack itemStack = item.buildItem(1);

        player.getInventory().addItem(itemStack);

    }
}
