package gg.supervisor.core.adapters.types.world;

import com.google.gson.TypeAdapter;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.supervisor.core.annotation.Adapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
@Adapter
public class LocationTypeAdapter extends TypeAdapter<Location> {

    @Override
    public void write(JsonWriter out, Location location) throws IOException {
        if (location == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        out.name("world").value(location.getWorld() != null ? location.getWorld().getName() : null);
        out.name("x").value(location.getX());
        out.name("y").value(location.getY());
        out.name("z").value(location.getZ());
        out.name("yaw").value(location.getYaw());
        out.name("pitch").value(location.getPitch());
        out.endObject();
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String worldName = null;
        double x = 0, y = 0, z = 0;
        float yaw = 0, pitch = 0;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "world":
                    worldName = in.nextString();
                    break;
                case "x":
                    x = in.nextDouble();
                    break;
                case "y":
                    y = in.nextDouble();
                    break;
                case "z":
                    z = in.nextDouble();
                    break;
                case "yaw":
                    yaw = (float) in.nextDouble();
                    break;
                case "pitch":
                    pitch = (float) in.nextDouble();
                    break;
                default:
                    // Skip any unexpected properties in the JSON
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        // Check if worldName was read successfully
        if (worldName == null) {
            throw new JsonParseException("Location's world name is missing in the JSON data");
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new JsonParseException("Unknown world: " + worldName);
        }

        return new Location(world, x, y, z, yaw, pitch);
    }
}
