package gg.supervisor.storage.sql;

import com.zaxxer.hikari.HikariDataSource;
import gg.supervisor.storage.sql.annotations.Table;
import gg.supervisor.storage.sql.annotations.Column;
import gg.supervisor.storage.sql.annotations.PrimaryKey;
import gg.supervisor.storage.sql.annotations.NotNull;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TableCreator {

    /**
     * Creates a database table based on the annotations in the entity class.
     * This method supports the creation of tables with composite primary keys.
     *
     * @param clazz the entity class annotated with @Table, @Column, etc.
     * @param dataSource the database connection to use for SQL execution
     * @throws SQLException if an SQL error occurs during table creation
     */
    public static void createTable(Class<?> clazz, HikariDataSource dataSource) throws SQLException {
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("The class must have a @Table annotation");
        }

        Table table = clazz.getAnnotation(Table.class);
        StringBuilder createStatement = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        createStatement.append(table.value()).append(" (");

        List<String> primaryKeyFields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                createStatement.append(column.value()).append(" ").append(getSQLType(field));

                if (field.isAnnotationPresent(NotNull.class)) {
                    createStatement.append(" NOT NULL");
                }

                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    primaryKeyFields.add(column.value());
                }

                createStatement.append(", ");
            }
        }

        if (!primaryKeyFields.isEmpty()) {
            createStatement.append("PRIMARY KEY (");
            createStatement.append(String.join(", ", primaryKeyFields));
            createStatement.append(")");
        } else {
            createStatement.setLength(createStatement.length() - 2);
        }

        createStatement.append(");");

        try (Statement statement = dataSource.getConnection().createStatement()) {
            statement.execute(createStatement.toString());
        }
    }

    /**
     * Maps Java types to SQL data types.
     * @param field the field whose type needs to be mapped to SQL
     * @return a String representing the SQL type
     */
    private static String getSQLType(Field field) {
        Class<?> type = field.getType();
        if (Integer.class == type || int.class == type) {
            return "INT";
        } else if (Double.class == type || double.class == type) {
            return "DOUBLE PRECISION";
        } else if (Float.class == type || float.class == type) {
            return "FLOAT";
        } else if (Long.class == type || long.class == type) {
            return "BIGINT";
        } else if (String.class == type) {
            return "VARCHAR(255)";
        }
        throw new IllegalArgumentException("Unsupported data type: " + type.getSimpleName());
    }
}
