package gg.supervisor.util.misc;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MapUtil {

    public static <T, E> Optional<T> getKeyByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    

}
