package gg.supervisor.adapters.types.world;

import com.google.gson.TypeAdapter;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.supervisor.api.Adapter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;

@Adapter
public class WorldTypeAdapter extends TypeAdapter<World> {

    @Override
    public void write(JsonWriter out, World world) throws IOException {
        if (world == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        out.name("name").value(world.getName());
        out.endObject();
    }

    @Override
    public World read(JsonReader in) throws IOException {
        if (in.peek().name().equals("NULL")) {
            in.nextNull();
            return null;
        }

        in.beginObject();
        String worldName = null;

        while (in.hasNext()) {
            if (in.nextName().equals("name")) {
                worldName = in.nextString();
            }
        }
        in.endObject();

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new JsonParseException("Unknown world: " + worldName);
        }
        return world;
    }
}
