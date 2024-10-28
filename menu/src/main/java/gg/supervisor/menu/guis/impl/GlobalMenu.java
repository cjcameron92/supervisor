package gg.supervisor.menu.guis.impl;

import gg.supervisor.menu.entities.GuiType;
import gg.supervisor.menu.entities.InteractionModifier;
import gg.supervisor.menu.guis.BaseGui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Represents a global, static menu GUI that is identical for all players.
 * This menu is designed to display non-dynamic content, meaning that
 * all items and interactions remain the same regardless of the player viewing it.
 *
 * <p>This class should be used in cases where no player-specific customization
 * is required. Once the menu items are populated, they should not vary between
 * different players.</p>
 */
public class GlobalMenu extends BaseGui {

    /**
     * Constructs a GlobalMenu instance with a specified GUI type, title, and interaction modifiers.
     * The content within this menu is static, meaning it is the same for all players.
     *
     * @param guiType              the type of GUI represented by this menu
     * @param title                the title displayed on the GUI, which remains constant
     * @param interactionModifiers the set of interaction modifiers defining interactions, uniform across players
     */
    public GlobalMenu(@NotNull GuiType guiType, @NotNull Component title, @NotNull Set<InteractionModifier> interactionModifiers) {
        super(guiType, title, interactionModifiers);
    }

    /**
     * Constructs a GlobalMenu instance with a specified number of rows, title, and interaction modifiers.
     * The content within this menu is static, meaning it is the same for all players.
     *
     * @param rows                 the number of rows in the GUI, fixed for all users
     * @param title                the title displayed on the GUI, which remains constant
     * @param interactionModifiers the set of interaction modifiers defining interactions, uniform across players
     */
    public GlobalMenu(int rows, @NotNull Component title, @NotNull Set<InteractionModifier> interactionModifiers) {
        super(rows, title, interactionModifiers);
    }

    /**
     * Opens this GlobalMenu for the specified player.
     * If the player is sleeping, the menu will not open.
     * Populates the GUI with static items that are identical for all players.
     *
     * <p>This method ensures that the same menu structure is presented to every player,
     * making it suitable for global interactions where no player-specific changes are required.</p>
     *
     * @param player the player for whom this GUI will be opened
     */
    @Override
    public void open(@NotNull HumanEntity player) {
        if (player.isSleeping()) return;

        if (firstRedraw) {
            populateGui();
            redraw();
            firstRedraw = false;
        }

        player.openInventory(getInventory());
    }
}
