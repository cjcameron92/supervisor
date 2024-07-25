package gg.supervisor.redis;

import gg.supervisor.redis.example.Player;
import gg.supervisor.redis.example.PlayerRepository;

import java.io.IOException;
import java.util.UUID;

public class Application {
    public static void main(String[] args) {
        // Initialize Redis connection
        Redis.init("127.0.0.1", 6379);

        // Create a dynamic proxy for PlayerRepository
        RedisProxyHandler<PlayerRepository> handler = new RedisProxyHandler<>(PlayerRepository.class);
        PlayerRepository playerService = handler.getInstance();

        playerService.find("player:1").thenAccept(player -> System.out.println(player.getName()));

        try {
            Thread.sleep(2000);;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
