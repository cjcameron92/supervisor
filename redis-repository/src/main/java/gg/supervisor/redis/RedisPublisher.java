package gg.supervisor.redis;


import gg.supervisor.api.Services;

public interface RedisPublisher {

    default void publish(Object object, Class<? extends RedisMessageListener<?>> listener) {
        Services.loadIfPresent(RedisLoader.class).newPublish().publish(object, listener);
    }
}
