package gg.supervisor.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Redis {

    private final JedisPool pool;
    private final String password;
    private final Set<RedisMessageListener<?>> subscriptions;

    public Redis(String host, int port, String password) {
        this.password = password;
        this.subscriptions = new HashSet<>();

        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        // TODO: 2024-07-27 implement general rules

        this.pool = new JedisPool(poolConfig, host, port);
    }

    public Jedis getResource() {
        final Jedis jedis = pool.getResource();
        if (password == null || password.isEmpty())
            jedis.auth(password);

        return jedis;
    }

    public Set<RedisMessageListener<?>> getSubscriptions() {
        return subscriptions;
    }
}
