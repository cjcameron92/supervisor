package gg.supervisor.storage.test;

import gg.supervisor.api.Storage;
import gg.supervisor.storage.JsonStorage;
import gg.supervisor.storage.JsonStorageService;

@Storage(fileName = "storage.json", service = JsonStorageService.class, type = Dummy.class)
public interface DummyStorage extends JsonStorage<Dummy> {


}
