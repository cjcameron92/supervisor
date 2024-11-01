package gg.supervisor.menu.guis.impl;

import gg.supervisor.menu.entities.GuiType;
import gg.supervisor.menu.entities.InteractionModifier;
import gg.supervisor.menu.exception.MenuException;
import gg.supervisor.menu.guis.BaseGui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * The {@code PersonalizedMenu} class represents a player-specific menu GUI that
 * provides a unique menu experience per player. Unlike static menus, such as {@link GlobalMenu},
 * each instance of {@code PersonalizedMenu} can be customized based on the assigned player.
 *
 * <p>This class is useful when the content or interactions of the menu need to vary
 * according to the individual player, in contrast to {@link GlobalMenu} which displays
 * identical content to all players.</p>
 */
public class PersonalizedMenu extends BaseGui {

    /**
     * The player for whom this menu is personalized.
     */
    protected final @Getter Player player;

    private @Getter boolean invalid = false;

    /**
     * Constructs a PersonalizedMenu instance with a specified GUI type, title, and interaction modifiers.
     * This menu is designed to be customized based on the assigned player.
     *
     * @param guiType              the type of GUI represented by this menu
     * @param title                the title displayed on the GUI
     * @param interactionModifiers the set of interaction modifiers defining how the player can interact with this menu
     */
    public PersonalizedMenu(@NotNull GuiType guiType, @NotNull Component title, @NotNull Set<InteractionModifier> interactionModifiers, Player player) {
        super(guiType, title, interactionModifiers);

        this.player = player;
    }

    /**
     * Constructs a PersonalizedMenu instance with a specified number of rows, title, and interaction modifiers.
     * This menu is designed to be customized based on the assigned player.
     *
     * @param rows                 the number of rows in the GUI
     * @param title                the title displayed on the GUI
     * @param interactionModifiers the set of interaction modifiers defining how the player can interact with this menu
     */
    public PersonalizedMenu(int rows, @NotNull Component title, @NotNull Set<InteractionModifier> interactionModifiers, Player player) {
        super(rows, title, interactionModifiers);

        this.player = player;
    }

    /**
     * Opens this PersonalizedMenu for the specified player.
     *
     * <p>This method ensures that the menu is only open for one player at a time.
     * If a different player attempts to open the menu when it is already assigned to
     * another, a {@link MenuException} is thrown.</p>
     *
     * <p>The method also clears the existing menu inventory, populates it based on the
     * current player's data, and initiates a redraw.</p>
     *
     * @throws MenuException if the menu is already assigned to a different player
     */

    public void open() {
        if (invalid)
            throw new MenuException("This menu was invalidated.");

        getInventory().clear();
        super.open(player, true);
    }

    @Override
    public void onClose() {
        invalid = true;
    }
}
