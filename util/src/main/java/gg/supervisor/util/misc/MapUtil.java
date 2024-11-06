package gg.supervisor.util.misc;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MapUtil {

    /**
     * Retrieves the key from a Map associated with the specified value.
     *
     * @param <T> the type of keys in the map
     * @param <E> the type of values in the map
     * @param map the Map to search for the specified value
     * @param value the value to find the corresponding key for
     * @return an Optional containing the key corresponding to the specified value, or empty if not found
     */
    public static <T, E> Optional<T> getKeyByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    /**
     * Filter a map by the values that satisfy the given predicate.
     *
     * @param map the map to filter
     * @param predicate the predicate used to filter the values
     * @return a new map containing only the entries whose values satisfy the predicate
     */
    public static <K, V> Map<K, V> filterByValue(Map<K, V> map, Predicate<V> predicate) {
        return map.entrySet()
                .stream()
                .filter(entry -> predicate.test(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Sorts the given map by the values in ascending order.
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map, which must be comparable
     * @param map the map to be sorted by values
     * @return a new LinkedHashMap containing the entries of the input map sorted by values in ascending order
     */
    public static <K, V extends Comparable<V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * Inverts the given map by swapping the keys with the values.
     *
     * @param <K> the type of keys in the original map
     * @param <V> the type of values in the original map
     * @param map the map to invert
     * @return a new Map with the keys and values from the original map swapped
     */
    public static <K, V> Map<V, K> invertMap(Map<K, V> map) {
        Map<V, K> inverted = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            inverted.put(entry.getValue(), entry.getKey());
        }
        return inverted;
    }


}
