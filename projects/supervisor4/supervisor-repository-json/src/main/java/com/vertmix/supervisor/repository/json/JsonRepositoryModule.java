package com.vertmix.supervisor.repository.json;

import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.core.annotation.Navigation;
import com.vertmix.supervisor.core.module.Module;
import com.vertmix.supervisor.core.service.Services;
import com.vertmix.supervisor.reflection.AbstractProxyHandler;

import java.io.File;
import java.lang.reflect.Proxy;

public class JsonRepositoryModule implements Module<Object> {

    private File folder = null;

    @Override
    public void onEnable(CoreProvider<Object> provider) {
        folder = provider.getPath().toFile();
        System.out.println("Enabled Json module");
        Services.register(JsonRepository.class, clazz -> {
            File file = folder;
            Navigation navigation = clazz.getAnnotation(Navigation.class);
            if (navigation != null) {
                file = new File(navigation.path());
            }


            return newRepository(clazz, new JsonProxyHandler<>(clazz, file));
        });


    }

    @Override
    public void onDisable() {

    }

    public static <T> JsonRepository<T> newRepository(Class<T> clazz, AbstractProxyHandler<T> handler) {
        return (JsonRepository<T>) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                handler
        );
    }

    public static <P, T> JsonPlayerRepository<P, T> newPlayerRepository(Class<T> clazz, AbstractProxyHandler<T> handler) {
        return (JsonPlayerRepository<P,T>) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                handler
        );
    }

}
