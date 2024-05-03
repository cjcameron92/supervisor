package gg.supervisor.storage.sql;

import gg.supervisor.storage.sql.annotations.Column;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to map SQL ResultSet rows to Java objects using annotations.
 */
public class ResultSetMapper {

    /**
     * Maps the first row of a ResultSet to an instance of the provided class type.
     * 
     * @param <T>   the type parameter corresponding to the class we want to map the ResultSet to
     * @param rs    the ResultSet from which to map the object
     * @param clazz the Class of the object to which the ResultSet should be mapped
     * @return an instance of T populated with data from the ResultSet
     * @throws SQLException           if a database access error occurs or this method is called on a closed result set
     * @throws InstantiationException if the class that declares the underlying constructor represents an abstract class
     * @throws IllegalAccessException if the underlying constructor is inaccessible
     */
    public static <T> T mapResultSetToObject(ResultSet rs, Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException {
        if (!rs.next()) {
            return null;
        }
        T instance = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);
                Object value = rs.getObject(column.value());
                field.set(instance, value);
            }
        }
        return instance;
    }

    /**
     * Maps all rows of a ResultSet to a list of instances of the provided class type.
     * 
     * @param <T>   the type parameter corresponding to the class we want to map the ResultSet to
     * @param rs    the ResultSet from which to map the objects
     * @param clazz the Class of the objects to which the ResultSet should be mapped
     * @return a list of instances of T populated with data from the ResultSet
     * @throws SQLException           if a database access error occurs or this method is called on a closed result set
     * @throws InstantiationException if the class that declares the underlying constructor represents an abstract class
     * @throws IllegalAccessException if the underlying constructor is inaccessible
     */
    public static <T> List<T> mapResultSetToList(ResultSet rs, Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException {
        List<T> list = new ArrayList<>();
        while (rs.next()) {
            T instance = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    field.setAccessible(true);
                    Column column = field.getAnnotation(Column.class);
                    Object value = rs.getObject(column.value());
                    field.set(instance, value);
                }
            }
            list.add(instance);
        }
        return list;
    }
}
