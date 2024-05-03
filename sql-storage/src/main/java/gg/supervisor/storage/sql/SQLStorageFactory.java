package gg.supervisor.storage.sql;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Factory for creating SQLStorage instances that are backed by a dynamic proxy,
 * allowing automatic handling of CRUD operations based on method annotations.
 */
public class SQLStorageFactory {

    /**
     * Creates a new instance of SQLStorage for the specified entity class using a dynamic proxy.
     *
     * @param entityClass The class of the entity for which SQLStorage is to be created.
     * @param dataSource  The HikariDataSource providing database connections.
     * @param <T>         The type of the entity.
     * @return An instance of SQLStorage<T> that handles database operations for T.
     */
    public static <T> SQLStorage<T> create(Class<T> entityClass, HikariDataSource dataSource) {
        SQLStorageHandler handler = new SQLStorageHandler(dataSource, entityClass);
        return (SQLStorage<T>) Proxy.newProxyInstance(
                SQLStorageFactory.class.getClassLoader(),
                new Class<?>[]{SQLStorage.class},
                handler
        );
    }
}