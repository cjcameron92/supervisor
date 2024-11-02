package gg.supervisor.menu.util;

import gg.supervisor.menu.guis.BaseGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Utility class for managing menus in a Minecraft plugin.
 */
public class MenuUtil {

    /**
     * Closes all open menus for all online players.
     * This method iterates through all online players and closes any open inventory menus they have.
     * If specified, it can trigger a close action and/or forwarding action on the menus being closed.
     */
    public static void closeAllMenus() {
        closeAllMenus(false);
    }

    /**
     * Closes all open menus for all online players and triggers a close action if specified.
     * This method iterates over all online players closing open inventory menus while enabling a triggering of close action for specified menus
     *
     * @param triggerCloseAction specifies whether to trigger a close action
     */
    public static void closeAllMenus(boolean triggerCloseAction) {
        closeAllMenus(triggerCloseAction, false);
    }


    /**
     * Closes all open menus for all online players.
     * This method iterates through all online players and closes any open inventory menus they have.
     * If specified, it can trigger a close action and/or forwarding action on the menus being closed.
     *
     * @param triggerCloseAction specifies whether to trigger a close action on the menus being closed
     * @param triggerForwarding specifies whether to trigger a forwarding action on the menus being closed
     */
    public static void closeAllMenus(boolean triggerCloseAction, boolean triggerForwarding) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            Optional.ofNullable(onlinePlayer.getOpenInventory().getTopInventory().getHolder()).ifPresent(inventoryHolder -> {
                if (inventoryHolder instanceof BaseGui baseGui)
                    baseGui.close(onlinePlayer, triggerCloseAction, triggerCloseAction);
            });

        }
    }

}
