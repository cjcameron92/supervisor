package com.vertmix.supervisor.configuration.toml;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.vertmix.supervisor.configuration.AbstractConfigService;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class TomlConfigService extends AbstractConfigService {

    private static final String EXT = ".toml";
    private static final TomlWriter TOML_WRITER = new TomlWriter();

    @Override
    public String getExtension() {
        return EXT;
    }

    @Override
    public void save(Object obj, File file) {
        trySave(obj, file, f -> file.getName().endsWith(EXT), (o, f) -> {
            try {
                TOML_WRITER.write(o, f);
            } catch (IOException e) {
                throw new RuntimeException("Error writing to TOML file: " + f, e);
            }
        });
    }

    @Override
    public <Type> Optional<Type> load(Class<Type> clazz, File file) {
        // Check if the file exists and is not empty
        if (!file.exists() || file.length() == 0) {
            return Optional.empty(); // Or handle this scenario as you see fit
        }

        return Optional.ofNullable(tryLoad(clazz, file, f -> f.getName().endsWith(EXT), ((f, instance) -> {
            Toml toml = new Toml().read(f);
            return toml.to(clazz);
        })));
    }
}
