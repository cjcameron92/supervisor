package gg.supervisor.menu.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class ItemContainers {

    public static ItemStack applySignature(ItemStack itemStack) {
        final ItemStack item = itemStack.clone();
        item.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(new NamespacedKey("supervisor", "item"), PersistentDataType.BOOLEAN, true));
        return item;
    }

    public static ItemStack applyValue(ItemStack itemStack, String key, String value) {
        final ItemStack item = itemStack.clone();
        item.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(new NamespacedKey("supervisor", key), PersistentDataType.STRING, value));
        return item;
    }

    @Nullable
    public static String getValue(ItemStack itemStack, String key) {

        if (!itemStack.hasItemMeta())
            return null;

        return itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("supervisor", key), PersistentDataType.STRING);

    }

}
