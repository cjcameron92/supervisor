package gg.supervisor.dummy.plugin.repository;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.dummy.plugin.model.Dog;
import gg.supervisor.repository.mongo.MongoRepository;

@Component
public interface CookieRepository extends MongoRepository<Dog> {

}
