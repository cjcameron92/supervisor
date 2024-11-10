package com.vertmix.supervisor.configuration.toml;

import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.core.module.Module;
import com.vertmix.supervisor.core.service.Services;

public class TomlConfigModule implements Module<Object> {

    @Override
    public void onEnable(CoreProvider<Object> provider) {
        Services.register(TomlConfigService.class, new TomlConfigService());
    }

    @Override
    public void onDisable() {

    }
}
