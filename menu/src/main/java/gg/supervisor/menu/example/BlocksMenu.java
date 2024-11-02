package gg.supervisor.menu.example;

import gg.supervisor.menu.entities.InteractionModifier;
import gg.supervisor.menu.guis.Pager;
import gg.supervisor.menu.guis.builder.SchemaBuilder;
import gg.supervisor.menu.guis.impl.PersonalizedMenu;
import gg.supervisor.menu.item.MenuItem;
import gg.supervisor.util.chat.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.stream.Stream;

public class BlocksMenu extends PersonalizedMenu {
    private static final List<MenuItem> gradient;

    static {
        gradient = Stream.of(Material.values()).filter(Material::isItem).map(x -> new MenuItem(x, event -> {
            event.getWhoClicked().closeInventory();
        })).toList();
    }

    private final BukkitTask task;

    public BlocksMenu(Plugin plugin, Player player) {
        super(player, 6, Text.translate(""), InteractionModifier.VALUES);

        getDecorator().decorate(
                new SchemaBuilder()
                        .add("xxxxxxxxx", 6)
                        .build()
        );

        final Pager first = new Pager(this, 'x').step(100).endless(Pager.EndlessType.SIMPLE);

        first.add(gradient);

        updateTitle(Text.translate("&cBlocks! &7(" + first.getPage() + "/" + first.getTotalPages() + ")"));

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            first.next();

            updateTitle(Text.translate("&cBlocks! &7(" + first.getPage() + "/" + first.getTotalPages() + ")"));
        }, 10L, 10L);
    }

    @Override
    public void onClose() {
        super.onClose();

        task.cancel();
        System.out.println("Cancelled task");

    }
}
