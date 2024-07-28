package gg.supervisor.redis;

import org.jetbrains.annotations.NotNull;

public interface RedisMessageListener<T> {

    void onReceive(@NotNull T type);


}
