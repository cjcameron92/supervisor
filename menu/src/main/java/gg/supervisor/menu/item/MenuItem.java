package gg.supervisor.menu.item;

import com.google.common.base.Preconditions;
import gg.supervisor.menu.action.GuiAction;
import gg.supervisor.menu.util.ItemContainers;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("unused")
public class MenuItem {

    @Getter
    private final UUID uuid = UUID.randomUUID();

    @Getter
    @Setter
    private GuiAction<InventoryClickEvent> action;

    @Getter
    private ItemStack itemStack;

    public MenuItem(@NotNull final ItemStack itemStack, @Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");

        this.action = action;
        setItemStack(itemStack);
    }

    public MenuItem(@NotNull final ItemStack itemStack) {
        this(itemStack, null);
    }

    public MenuItem(@NotNull final Material material) {
        this(new ItemStack(material), null);
    }

    public MenuItem(@NotNull final Material material, @Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        this(new ItemStack(material), action);
    }

    public void setItemStack(@NotNull final ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");

        this.itemStack = itemStack.getType().isAir() ? itemStack.clone() : ItemContainers.applyValue(itemStack, "menu-item", uuid.toString());

    }

}
