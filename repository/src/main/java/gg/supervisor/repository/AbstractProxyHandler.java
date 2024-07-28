package gg.supervisor.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.supervisor.repository.adapter.GeneralTypeAdapterFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public abstract class AbstractProxyHandler<T> implements ProxyHandler<T> {

    protected final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .registerTypeAdapterFactory(new GeneralTypeAdapterFactory())
            .create();
    protected final Class<T> serviceInterface;
    protected final Type entityType;

    public AbstractProxyHandler(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
        this.entityType = ((ParameterizedType) serviceInterface.getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    public T getInstance() {
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                this
        );
    }

}
