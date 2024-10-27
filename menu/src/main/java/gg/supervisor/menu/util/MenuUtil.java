package gg.supervisor.menu.util;

import gg.supervisor.menu.guis.BaseGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class MenuUtil {

    public static void closeAllMenus() {
        closeAllMenus(false);
    }

    public static void closeAllMenus(boolean triggerCloseAction) {
        closeAllMenus(triggerCloseAction, false);
    }

    public static void closeAllMenus(boolean triggerCloseAction, boolean triggerForwarding) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            Optional.ofNullable(onlinePlayer.getOpenInventory().getTopInventory().getHolder()).ifPresent(inventoryHolder -> {
                if (inventoryHolder instanceof BaseGui baseGui)
                    baseGui.close(onlinePlayer, triggerCloseAction, triggerCloseAction);
            });

        }
    }

}
