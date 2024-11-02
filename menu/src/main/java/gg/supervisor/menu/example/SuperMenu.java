package gg.supervisor.menu.example;

import gg.supervisor.menu.builder.ItemBuilder;
import gg.supervisor.menu.entities.InteractionModifier;
import gg.supervisor.menu.guis.Pager;
import gg.supervisor.menu.guis.impl.GlobalMenu;
import gg.supervisor.menu.item.MenuItem;
import gg.supervisor.util.chat.Text;
import org.bukkit.Material;

public class SuperMenu extends GlobalMenu {

    public SuperMenu() {
        super(5, Text.translate("&c Multi Page test"), InteractionModifier.VALUES);

        getDecorator().decorate(
                "fffffffff",
                "{xxxxxxx}", // Pager 1
                "<sssssss>", // Pager 2,
                "-zzzzzzz_", // Pager 3
                "fffffffff");

        getDecorator().set('f', ItemBuilder.from(Material.RED_STAINED_GLASS_PANE).name("&c").build());

        final Pager firstRow = new Pager(this, 'x');
        firstRow.endless(Pager.EndlessType.NONE);

        for (int i = 1; i <= 3; i++) { // dummy items
            firstRow.quietlyAdd(new MenuItem(ItemBuilder.from(Material.DIRT).setAmount(i).name("&cDirt #" + i).build(), event ->
                    firstRow.add(
                            new MenuItem(ItemBuilder.from(Material.YELLOW_BANNER).name("ADDED ").build(), e -> firstRow.remove(getMenuItem(e.getSlot()))),
                            new MenuItem(ItemBuilder.from(Material.BLACK_BANNER).name("ADDED ").build(), e -> firstRow.remove(getMenuItem(e.getSlot()))),
                            new MenuItem(ItemBuilder.from(Material.RED_BANNER).name("ADDED ").build(), e -> firstRow.remove(getMenuItem(e.getSlot()))),
                            new MenuItem(ItemBuilder.from(Material.CYAN_BANNER).name("ADDED ").build(), e -> firstRow.remove(getMenuItem(e.getSlot())))
                    )
            ));
        }

        final Pager secondRow = new Pager(this, 's');
        secondRow.endless(Pager.EndlessType.SIMPLE);

        for (int i = 1; i <= 64; i++) { // dummy items
            secondRow.quietlyAdd(new MenuItem(ItemBuilder.from(Material.DIAMOND).setAmount(i).name("&dDiamond #" + i).build()));
        }

        final Pager thirdRow = new Pager(this, 'z');
        thirdRow.endless(Pager.EndlessType.TRULY_ENDLESS);

        for (int i = 1; i <= 64; i++) { // dummy items
            thirdRow.quietlyAdd(new MenuItem(ItemBuilder.from(Material.EMERALD).setAmount(i).name("&aEmerald #" + i).build()));
        }

        // for such a function it's better to add quietly rather than normally since it'd trigger an update less often, you'd trigger an update (pageSize - currentItems.size()) times,
        // While we have checks in place to prevent that, where it only updates if the item will be displayed on the current or any previous page.
        // utilizing quietlyAdd is better since you do not run all the logic to detect such behavior ^ It is meant to be a failsafe to make developer's life easier when it comes to updating and removing.

        firstRow.updatePage();
        secondRow.updatePage();
        thirdRow.updatePage();

        getDecorator().set('{', new MenuItem(ItemBuilder.from(Material.ARROW).name("Prev").build(), event -> firstRow.previous()));
        getDecorator().set('}', new MenuItem(ItemBuilder.from(Material.ARROW).name("Next").build(), event -> firstRow.next()));

        getDecorator().set('<', new MenuItem(ItemBuilder.from(Material.ARROW).name("Prev").build(), event -> secondRow.previous()));
        getDecorator().set('>', new MenuItem(ItemBuilder.from(Material.ARROW).name("Next").build(), event -> secondRow.next()));

        getDecorator().set('-', new MenuItem(ItemBuilder.from(Material.ARROW).name("Prev").build(), event -> thirdRow.previous()));
        getDecorator().set('_', new MenuItem(ItemBuilder.from(Material.ARROW).name("Next").build(), event -> thirdRow.next()));

    }
}
