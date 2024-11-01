package gg.supervisor.core.adapters.types.world;

import com.google.gson.TypeAdapter;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.supervisor.core.annotation.Adapter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.IOException;

@Adapter
public class BlockTypeAdapter extends TypeAdapter<Block> {

    @Override
    public void write(JsonWriter out, Block block) throws IOException {
        if (block == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        out.name("world").value(block.getWorld().getName());
        out.name("x").value(block.getX());
        out.name("y").value(block.getY());
        out.name("z").value(block.getZ());
        out.endObject();
    }

    @Override
    public Block read(JsonReader in) throws IOException {
        if (in.peek().name().equals("NULL")) {
            in.nextNull();
            return null;
        }

        in.beginObject();
        String worldName = null;
        int x = 0, y = 0, z = 0;

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "world":
                    worldName = in.nextString();
                    break;
                case "x":
                    x = in.nextInt();
                    break;
                case "y":
                    y = in.nextInt();
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
        return world.getBlockAt(x, y, z);
    }
}
