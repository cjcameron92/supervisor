package gg.supervisor.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {

    private static JedisPool pool;

    public static void init(String host, int port) {
        pool = new JedisPool(new JedisPoolConfig(), host, port);
    }

    public static Jedis getResource() {
        return pool.getResource();
    }
}
