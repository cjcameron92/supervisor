package gg.supervisor.repository.mongo;

import gg.supervisor.core.repository.JsonPlayerRepository;
import gg.supervisor.core.repository.PlayerRepository;

public interface MongoPlayerRepository<T> extends JsonPlayerRepository<T>, PlayerRepository<T> {

}
