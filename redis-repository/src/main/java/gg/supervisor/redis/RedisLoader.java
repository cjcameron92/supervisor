package gg.supervisor.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.supervisor.api.Component;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedisLoader {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private final Redis redis;
    private final Map<String, String> channelCache = new ConcurrentHashMap<>();
    private final Map<String, Type> cachedTypes = new ConcurrentHashMap<>();

    public RedisLoader(Redis redis) {
        this.redis = redis;
        redis.getResource().subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (channelCache.containsKey(channel)) {
                    final RedisMessageListener<?> redisMessageListener = redis.getSubscriptions().stream().filter(listener -> listener.getClass().getSimpleName().equalsIgnoreCase(channel)).findAny().orElseThrow();
                    if (cachedTypes.containsKey(channel)) {
                        redisMessageListener.onReceive(GSON.fromJson(message, cachedTypes.get(channel)));
                    } else {
                       final Type type = ((ParameterizedType) redisMessageListener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                       cachedTypes.put(channel, type);
                       redisMessageListener.onReceive(GSON.fromJson(message, type));
                    }

                } else {
                    final Optional<RedisMessageListener<?>> listenerOptional = redis.getSubscriptions().stream().filter(redisMessageListener -> redisMessageListener.getClass().getSimpleName().equalsIgnoreCase(channel)).findAny();
                    if (listenerOptional.isPresent()) {
                        final RedisMessageListener<?> redisMessageListener = listenerOptional.get();
                        if (cachedTypes.containsKey(channel)) {
                            redisMessageListener.onReceive(GSON.fromJson(message, cachedTypes.get(channel)));
                        } else {
                            final Type type = ((ParameterizedType) redisMessageListener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                            cachedTypes.put(channel, type);
                            channelCache.put(channel, redisMessageListener.getClass().getSimpleName());
                            redisMessageListener.onReceive(GSON.fromJson(message, type));
                        }
                    }
                }

            }
        }, redis.getSubscriptions().stream().map(obj -> obj.getClass().getSimpleName()).toList().toArray(new String[0]));
    }

    public RedisPublisherApp newPublish() {
        return new RedisPublisherApp();
    }

    public class RedisPublisherApp implements RedisPublisher {

        @Override
        public void publish(Object object, Class<? extends RedisMessageListener<?>> listener) {
            CompletableFuture.runAsync(() -> redis.getResource().publish(GSON.toJson(object), listener.getSimpleName()));
        }
    }


    public Redis getRedis() {
        return redis;
    }
}
