package gg.supervisor.mongo.test;

import gg.supervisor.api.Storage;
import gg.supervisor.mongo.MongoServiceStorage;
import gg.supervisor.mongo.MongoStorage_V1;

@Storage(service = MongoServiceStorage.class, type = Dummy.class)
public interface DummyStorage extends MongoStorage_V1<Dummy> {


}
