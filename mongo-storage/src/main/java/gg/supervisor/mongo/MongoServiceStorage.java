package gg.supervisor.mongo;

import gg.supervisor.api.Services;
import gg.supervisor.api.Storage;
import gg.supervisor.api.StorageService;


import java.io.File;
import java.io.IOException;

public class MongoServiceStorage implements StorageService {

    @Override
    public Object loadService(Class<?> clazz) {
        Storage storage = clazz.getAnnotation(Storage.class);
        if (storage != null) {

        }
        return null;
    }
}
