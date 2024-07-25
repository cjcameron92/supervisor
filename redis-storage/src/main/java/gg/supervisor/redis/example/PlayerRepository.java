package gg.supervisor.redis.example;

import gg.supervisor.redis.RedisRepository;
import gg.supervisor.redis.annotation.Service;

@Service
public interface PlayerRepository extends RedisRepository<Player> {

}
