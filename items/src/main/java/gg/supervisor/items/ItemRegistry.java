package gg.supervisor.items;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private static final Map<String, Item> items = new HashMap<>();

    public static Map<String, Item> getItems() {
        return items;
    }
}
