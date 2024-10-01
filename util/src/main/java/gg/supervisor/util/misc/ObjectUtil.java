package gg.supervisor.util.misc;

import java.util.Optional;
import java.util.function.Consumer;

public class ObjectUtil {

    public static <T> void ifNotNull(T object, Consumer<T> consumer) {
        Optional.ofNullable(object).ifPresent(consumer);
    }
    
}
