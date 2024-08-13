package gg.supervisor.adapters.entity;

import com.google.gson.TypeAdapter;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.supervisor.api.Adapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

@Adapter
public class PlayerTypeAdapter extends TypeAdapter<Player> {

    @Override
    public void write(JsonWriter out, Player player) throws IOException {
        if (player == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        out.name("uuid").value(player.getUniqueId().toString());
        out.name("name").value(player.getName());
        out.endObject();
    }

    @Override
    public Player read(JsonReader in) throws IOException {
        if (in.peek().name().equals("NULL")) {
            in.nextNull();
            return null;
        }

        in.beginObject();
        UUID uuid = null;
        String name = null;

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "uuid":
                    uuid = UUID.fromString(in.nextString());
                    break;
                case "name":
                    name = in.nextString();
                    break;
            }
        }
        in.endObject();

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            throw new JsonParseException("Unknown player: " + name);
        }
        return player;
    }
}
