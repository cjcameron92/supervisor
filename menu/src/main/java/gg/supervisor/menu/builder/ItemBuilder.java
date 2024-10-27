package gg.supervisor.menu.builder;

import gg.supervisor.util.chat.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemBuilder {
    public ItemStack item;
    public ItemMeta itemMeta;

    ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.itemMeta = item.getItemMeta();
    }

    ItemBuilder(ItemStack item) {
        this.item = item;
        this.itemMeta = item.getItemMeta();
    }

    public static ItemBuilder from(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static ItemBuilder from(ItemStack item) {
        return new ItemBuilder(item);
    }


    public static ItemBuilder from(Material material) {
        return from(material, 1);
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        item.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder customModelData(int custom) {
        itemMeta.setCustomModelData(custom);
        return this;
    }

    public ItemBuilder name(String text) {
        itemMeta.displayName(Text.translate(text));
        return this;
    }

    public ItemBuilder name(Component component) {
        itemMeta.displayName(component);
        return this;
    }

    public ItemBuilder loreString(List<String> text) {
        itemMeta.lore(Text.translate(text));
        return this;
    }

    public ItemBuilder lore(List<Component> components) {
        itemMeta.lore(components);
        return this;
    }

    public ItemBuilder lore(Consumer<List<Component>> consumer) {
        List<Component> lore = itemMeta.lore() == null ? new ArrayList<>() : new ArrayList<>(Objects.requireNonNull(itemMeta.lore()));

        consumer.accept(lore);

        lore(lore);
        return this;
    }

    public ItemBuilder replaceLore(Function<String, String> replace) {
        if (this.itemMeta.lore() == null)
            return this;

        final List<String> oldLore = Text.translateToMiniMessage(Objects.requireNonNull(this.itemMeta.lore()));

        itemMeta.lore(Text.translate(oldLore.stream().map(replace).toList()));

        return this;
    }

    public ItemBuilder editMeta(Consumer<ItemMeta> meta) {
        meta.accept(itemMeta);
        return this;
    }

    public ItemBuilder editItem(Consumer<ItemStack> consumer) {
        consumer.accept(item);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }

    public ItemStack build(Function<String, String> replace) {
        return build(replace, $ -> {
        });
    }

    public ItemStack build(Function<String, String> replace, Consumer<ItemStack> consumer) {
        ItemStack duplicate = item.clone();
        ItemMeta meta = itemMeta.clone();

        if (meta.lore() != null) {
            final List<String> oldLore = Text.translateToMiniMessage(Objects.requireNonNull(meta.lore()));
            meta.lore(Text.translate(oldLore.stream().map(replace).toList()));
        }

        if (meta.displayName() != null) {
            String replaced = replace.apply(Text.translateToMiniMessage(Objects.requireNonNull(meta.displayName())));

            meta.displayName(Text.translate(replaced));

        }

        duplicate.setItemMeta(meta);

        consumer.accept(duplicate);

        return duplicate;
    }
}
