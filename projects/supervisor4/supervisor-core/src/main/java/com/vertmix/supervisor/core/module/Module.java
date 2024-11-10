package com.vertmix.supervisor.core.module;

import com.vertmix.supervisor.core.CoreProvider;

public interface Module<T> {
    void onEnable(CoreProvider<T> provider);

    void onDisable();


}
