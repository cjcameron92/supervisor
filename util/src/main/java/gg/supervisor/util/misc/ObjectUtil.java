package gg.supervisor.util.misc;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ObjectUtil {

    /**
     * Executes the specified operation on the given object if it is not null.
     *
     * @param object the object to check for null
     * @param consumer the operation to be executed if the object is not null
     */
    public static <T> void ifNotNull(T object, Consumer<T> consumer) {
        Optional.ofNullable(object).ifPresent(consumer);
    }


    /**
     * Checks if the provided object is null or empty.
     *
     * @param obj the object to check for null or empty condition
     * @return true if the object is null or empty (for String, Collection, Map), false otherwise
     */
    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) return true;
        if (obj instanceof String) return ((String) obj).isEmpty();
        if (obj instanceof Collection) return ((Collection<?>) obj).isEmpty();
        if (obj instanceof Map) return ((Map<?, ?>) obj).isEmpty();
        return false;
    }

    /**
     * Safely casts an object to the specified class if the object is an instance of that class.
     *
     * @param obj the object to be cast
     * @param clazz the class to cast the object to
     * @return the object cast to the specified class if successful, otherwise null
     */
    public static <T> T safeCast(Object obj, Class<T> clazz) {
        return clazz.isInstance(obj) ? clazz.cast(obj) : null;
    }

    /**
     * Executes the specified action if the given object is not null; otherwise, runs the emptyAction.
     *
     * @param object the object to check for null
     * @param action the action to be executed if the object is not null
     * @param emptyAction the runnable to be executed if the object is null
     */
    public static <T> void ifPresentOrElse(T object, Consumer<T> action, Runnable emptyAction) {
        if (object != null) {
            action.accept(object);
        } else {
            emptyAction.run();
        }
    }


}
