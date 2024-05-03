package gg.supervisor.storage.sql;

import com.zaxxer.hikari.HikariConfig;
import gg.supervisor.api.Services;
import gg.supervisor.api.Storage;
import gg.supervisor.api.StorageService;

import com.zaxxer.hikari.HikariDataSource;
import gg.supervisor.storage.sql.config.SQLConfig;

import java.sql.SQLException;

public class SQLStorageService implements StorageService {


    @Override
    public Object loadService(Class<?> clazz) {
        final Storage storage = clazz.getAnnotation(Storage.class);
        if (storage != null) {
            final Class<?> configClass = storage.config();
            final SQLConfig sqlConfig = (SQLConfig) Services.loadIfPresent(configClass);
            final HikariConfig config = new HikariConfig();

            sqlConfig.cacheProps.forEach(config::addDataSourceProperty);
            config.setUsername(sqlConfig.username);
            config.setPassword(sqlConfig.password);
            config.setJdbcUrl(sqlConfig.jdbcUrl);

            final Class<?> typeClass = storage.type();

            final HikariDataSource dataSource = new HikariDataSource(config);

            final SQLStorage<?> sqlStorage = SQLStorageFactory.create(typeClass, dataSource);
            try {
                TableCreator.createTable(typeClass, dataSource);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Services.register(clazz, sqlStorage);

            return sqlStorage;
        }
        throw new RuntimeException("Requires Storage annotation");
    }
}
