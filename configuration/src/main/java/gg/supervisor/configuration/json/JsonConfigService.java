package gg.supervisor.configuration.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.supervisor.configuration.AbstractConfigService;
import gg.supervisor.core.annotation.Component;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

@Component
public class JsonConfigService extends AbstractConfigService {

    private static final String EXT = ".json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    public JsonConfigService(Plugin plugin) {
        super();
    }

    @Override
    public String getExtension() {
        return EXT;
    }

    @Override
    public void save(Object obj, File file) {
        trySave(obj, file, f -> f.getName().endsWith(EXT), (o, f) -> {
            try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
                GSON.toJson(obj, obj.getClass(), writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public <Type> Optional<Type> load(Class<Type> clazz, File file) {
        return Optional.ofNullable(tryLoad(clazz, file, f -> f.getName().endsWith(EXT), (f, instance) -> {
            try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, clazz);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }));
    }
}
