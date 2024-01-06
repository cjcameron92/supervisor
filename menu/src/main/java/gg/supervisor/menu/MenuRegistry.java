package gg.supervisor.menu;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;


import java.util.concurrent.TimeUnit;

public class MenuRegistry {

    private final Cache<String, Menu> inventories = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();

    public Cache<String, Menu> getInventories() {
        return inventories;
    }
}
