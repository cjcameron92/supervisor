package com.vertmix.supervisor.configuration.yml;

import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.core.module.Module;
import com.vertmix.supervisor.core.service.Services;

public class YmlConfigModule implements Module<Object> {

    @Override
    public void onEnable(CoreProvider<Object> provider) {
        Services.register(YamlConfigService.class, new YamlConfigService());
    }

    @Override
    public void onDisable() {

    }
}
