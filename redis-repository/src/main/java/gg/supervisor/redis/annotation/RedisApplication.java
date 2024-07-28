package gg.supervisor.redis.annotation;

import org.bukkit.plugin.Plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RedisApplication {

    Class<? extends Plugin> plugin();
}
