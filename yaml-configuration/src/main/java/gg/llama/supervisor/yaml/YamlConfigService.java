package gg.llama.supervisor.yaml;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import gg.llama.supervisor.api.Component;
import gg.llama.supervisor.configuration.AbstractConfigService;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Component
public class YamlConfigService extends AbstractConfigService {

    private static final String EXT = ".yml";
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
            .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public YamlConfigService(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void save(Object obj, File file) {
        trySave(obj, file, f -> file.getName().endsWith(EXT), (o, f) -> {
            try {
                MAPPER.writeValue(f, o);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public <Type> Optional<Type> load(Class<Type> clazz, File file) {
        return Optional.ofNullable(tryLoad(clazz, file, f -> f.getName().endsWith(EXT), ((f, instance) -> {
            try {
                return MAPPER.readValue(f, clazz);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })));
    }
}
