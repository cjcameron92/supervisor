package gg.supervisor.menu.example;

import gg.supervisor.menu.entities.InteractionModifier;
import gg.supervisor.menu.guis.Pager;
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

public class GradientMenu extends PersonalizedMenu {
    private final BukkitTask task;


    public GradientMenu(Plugin plugin, Player player) {
        super(player, 6, Text.translate("&cGRADIENT!"), InteractionModifier.VALUES);

        final List<MenuItem> gradient = Stream.of(
                Material.WHITE_STAINED_GLASS_PANE,
                Material.LIGHT_GRAY_STAINED_GLASS_PANE,
                Material.GRAY_STAINED_GLASS_PANE,
                Material.BLACK_STAINED_GLASS_PANE,
                Material.BROWN_STAINED_GLASS_PANE,
                Material.RED_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS_PANE,
                Material.YELLOW_STAINED_GLASS_PANE,
                Material.LIME_STAINED_GLASS_PANE,
                Material.GREEN_STAINED_GLASS_PANE,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                Material.CYAN_STAINED_GLASS_PANE,
                Material.BLUE_STAINED_GLASS_PANE,
                Material.PURPLE_STAINED_GLASS_PANE,
                Material.MAGENTA_STAINED_GLASS_PANE,
                Material.PINK_STAINED_GLASS_PANE
        ).map(x -> new MenuItem(x, event -> new BlocksMenu(plugin, player).open())).toList();


        getDecorator().decorate(
                "xxxxxxxxx",
                "zzzzzzzzz",
                "aaaaaaaaa",
                "bbbbbbbbb",
                "ccccccccc",
                "ddddddddd"
        );

        final Pager first = new Pager(this, 'x').step(1).endless(Pager.EndlessType.TRULY_ENDLESS);
        final Pager second = new Pager(this, 'z').step(1).endless(Pager.EndlessType.TRULY_ENDLESS);
        final Pager third = new Pager(this, 'a').step(1).endless(Pager.EndlessType.TRULY_ENDLESS);
        final Pager fourth = new Pager(this, 'b').step(1).endless(Pager.EndlessType.TRULY_ENDLESS);
        final Pager fifth = new Pager(this, 'c').step(1).endless(Pager.EndlessType.TRULY_ENDLESS);
        final Pager sixth = new Pager(this, 'd').step(1).endless(Pager.EndlessType.TRULY_ENDLESS);

        first.add(gradient);
        second.add(gradient);
        third.add(gradient);
        fourth.add(gradient);
        fifth.add(gradient);
        sixth.add(gradient);

        second.setPage(1);
        third.setPage(2);
        fourth.setPage(3);
        fifth.setPage(4);
        sixth.setPage(5);

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            first.previous();
            second.previous();
            third.previous();
            fourth.next();
            fifth.next();
            sixth.next();
        }, 1L, 1L);
    }

    @Override
    public void onClose() {
        super.onClose();

        task.cancel();
        System.out.println("Cancelled task ");
    }
}
