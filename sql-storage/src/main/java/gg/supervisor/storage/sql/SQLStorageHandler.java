package gg.supervisor.storage.sql;

import com.zaxxer.hikari.HikariDataSource;
import gg.supervisor.storage.sql.annotations.Column;
import gg.supervisor.storage.sql.annotations.NotNull;
import gg.supervisor.storage.sql.annotations.PrimaryKey;
import gg.supervisor.storage.sql.annotations.Query;
import gg.supervisor.storage.sql.annotations.Table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles dynamic invocation of methods for SQL storage operations using reflection to automatically
 * generate and execute SQL statements based on class annotations.
 */
public class SQLStorageHandler implements InvocationHandler {
    private final HikariDataSource dataSource;
    private final Class<?> clazz;

    /**
     * Constructs an SQLStorageHandler with a specified database connection.
     *
     * @param dataSource the active database connection
     * @param clazz the target type
     */
    public SQLStorageHandler(HikariDataSource dataSource, Class<?> clazz) {
        this.dataSource = dataSource;
        this.clazz = clazz;
    }

    /**
     * Handles the dynamic invocation of any method called on the proxy instance.
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the Method instance corresponding to the interface method invoked
     * @param args   an array of objects containing the values of the arguments passed in the method invocation
     * @return a CompletableFuture that will be completed in the future with the result of the method invocation
     * @throws Throwable if there is an underlying exception
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isAnnotationPresent(Query.class)) {
            return handleCustomQuery(method, args);
        }

        String methodName = method.getName();
        Class<?> entityClass = method.getReturnType();  // Assuming the method's return type is the entity class

        return switch (methodName) {
            case "save" -> save(entityClass, args[0]);
            case "delete" -> delete(entityClass, args[0]);
            case "find" -> find(entityClass, args[0]);
            case "findAll" -> findAll(entityClass);
            default -> throw new IllegalStateException("Unexpected value: " + methodName);
        };
    }

    /**
     * Executes a custom query defined by the @Query annotation on the method.
     *
     * @param method the method annotated with @Query
     * @param args   the arguments to be used in the query
     * @return a CompletableFuture with the result of the query
     */
    private Object handleCustomQuery(Method method, Object[] args) {
        Query query = method.getAnnotation(Query.class);
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(query.value())) {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    stmt.setObject(i + 1, args[i]);
                }
            }
            ResultSet rs = stmt.executeQuery();
            return method.getReturnType().equals(Void.TYPE) ? null : ResultSetMapper.mapResultSetToList(rs, method.getReturnType());
        } catch (Exception e) {
            throw new RuntimeException("Error executing custom query", e);
        }

    }

    private Object save(Class<?> entityClass, Object entity) throws IllegalAccessException, SQLException {
        Table table = entityClass.getAnnotation(Table.class);
        StringBuilder sql = new StringBuilder("INSERT INTO " + table.value() + " (");
        StringBuilder placeholders = new StringBuilder();
        List<Object> values = new ArrayList<>();

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                if (field.isAnnotationPresent(NotNull.class) && field.get(entity) == null) {
                    throw new IllegalArgumentException("Null value found for NOT NULL field: " + field.getName());
                }

                field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);
                sql.append(column.value()).append(",");
                placeholders.append("?,");
                values.add(field.get(entity));
            }
        }

        sql.setLength(sql.length() - 1);  // Remove the last comma
        sql.append(") VALUES (").append(placeholders.substring(0, placeholders.length() - 1)).append(")");

        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                statement.setObject(i + 1, values.get(i));
            }
            statement.executeUpdate();
        }

        return null;
    }

    private Object delete(Class<?> entityClass, Object key) throws SQLException {
        Table table = entityClass.getAnnotation(Table.class);
        String primaryKeyColumn = findPrimaryKeyColumn(entityClass);
        String sql = "DELETE FROM " + table.value() + " WHERE " + primaryKeyColumn + " = ?";

        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setObject(1, key);
            statement.executeUpdate();
        }

        return null;
    }

    private Object find(Class<?> entityClass, Object key) throws SQLException, InstantiationException, IllegalAccessException {
        Table table = entityClass.getAnnotation(Table.class);
        String primaryKeyColumn = findPrimaryKeyColumn(entityClass);
        String sql = "SELECT * FROM " + table.value() + " WHERE " + primaryKeyColumn + " = ?";

        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            statement.setObject(1, key);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return ResultSetMapper.mapResultSetToObject(resultSet, entityClass);
            }
            return null;
        }
    }

    private List<Object> findAll(Class<?> entityClass) throws SQLException, InstantiationException, IllegalAccessException {
        Table table = entityClass.getAnnotation(Table.class);
        String sql = "SELECT * FROM " + table.value();
        List<Object> results = new ArrayList<>();

        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                results.add(ResultSetMapper.mapResultSetToObject(resultSet, entityClass));
            }
        }
        return results;
    }

    private String findPrimaryKeyColumn(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                Column column = field.getAnnotation(Column.class);
                return column.value();
            }
        }
        throw new IllegalStateException("No primary key found in class " + entityClass.getName());
    }
}
