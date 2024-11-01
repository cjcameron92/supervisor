package gg.supervisor.core.adapters.types.world;

import com.google.gson.TypeAdapter;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.supervisor.core.annotation.Adapter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.io.IOException;

@Adapter
public class ChunkTypeAdapter extends TypeAdapter<Chunk> {

    @Override
    public void write(JsonWriter out, Chunk chunk) throws IOException {
        if (chunk == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        out.name("world").value(chunk.getWorld().getName());
        out.name("x").value(chunk.getX());
        out.name("z").value(chunk.getZ());
        out.endObject();
    }

    @Override
    public Chunk read(JsonReader in) throws IOException {
        if (in.peek().name().equals("NULL")) {
            in.nextNull();
            return null;
        }

        in.beginObject();
        String worldName = null;
        int x = 0, z = 0;

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "world":
                    worldName = in.nextString();
                    break;
                case "x":
                    x = in.nextInt();
                    break;
                case "z":
                    z = in.nextInt();
                    break;
            }
        }
        in.endObject();

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new JsonParseException("Unknown world: " + worldName);
        }
        return world.getChunkAt(x, z);
    }
}
