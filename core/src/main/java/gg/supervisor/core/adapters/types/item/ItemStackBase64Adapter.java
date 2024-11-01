package gg.supervisor.core.adapters.types.item;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.supervisor.core.annotation.Adapter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Adapter
public class ItemStackBase64Adapter extends TypeAdapter<ItemStack> {

    @Override
    public void write(JsonWriter out, ItemStack itemStack) throws IOException {
        if (itemStack == null) {
            out.nullValue();
            return;
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream)) {

            bukkitObjectOutputStream.writeObject(itemStack);
            String base64 = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            out.value(base64);

        } catch (IOException e) {
            throw new IOException("Failed to serialize ItemStack to Base64", e);
        }
    }

    @Override
    public ItemStack read(JsonReader in) throws IOException {
        if (in.peek() == null) {
            in.nextNull();
            return null;
        }

        String base64 = in.nextString();
        byte[] data = Base64.getDecoder().decode(base64);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream)) {

            return (ItemStack) bukkitObjectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new IOException("Failed to deserialize ItemStack from Base64", e);
        }
    }
}
