package gg.supervisor.menu.builder;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class SkullBuilder extends ItemBuilder {

    private final SkullMeta skullMeta;

    public SkullBuilder() {
        super(Material.PLAYER_HEAD, 1);

        this.skullMeta = (SkullMeta) itemMeta;
    }


    public SkullBuilder setOwner(OfflinePlayer player) {
        skullMeta.setOwningPlayer(player);
        return this;
    }

    public SkullBuilder setTexture(String texture) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());

        profile.setProperty(new ProfileProperty("textures", texture));

        skullMeta.setPlayerProfile(profile);
        return this;
    }


}
