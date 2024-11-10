package com.vertmix.supervisor.menu;

import com.vertmix.supervisor.core.bukkit.item.Icon;
import com.vertmix.supervisor.reflection.AbstractProxyHandler;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuProxyHandler extends AbstractProxyHandler<Menu> {

    private final Map<Character, Icon> items = new HashMap<>();
    private final Map<Character, MenuAction> actions = new HashMap<>();
    private final List<String> schema = new ArrayList<>();
    private final Map<String, Object> options = new HashMap<>();

    private final File file;

    /**
     * Constructor for creating an instance of {@link AbstractProxyHandler}.
     *
     * @param serviceInterface The service interface that this proxy handler will implement.
     */
    public MenuProxyHandler(Class<Menu> serviceInterface, File file) {
        super(serviceInterface);
        this.file = file;
    }

    private void loadFile() {
        // TODO: 2024-11-09 load from {@link MenuData}
    }

    private void saveDefaultConfig() {
        MenuData data = new MenuData();
        data.items = items;
        data.options = options;
        data.schema = schema;

        // TODO: 2024-11-09 Implement service to save
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "set":
                char character = (char) args[0];
                Icon icon = (Icon) args[1];
                MenuAction action = (MenuAction) args[2];

                items.put(character, icon);
                actions.put(character, action);
                return null;
            case "redraw":
                if (file.exists()) {
                    loadFile();
                } else {
                    saveDefaultConfig();
                }
            default:
                // Unsupported operations
                throw new UnsupportedOperationException("Unsupported operation: " + method.getName());
        }
    }

    public static class MenuData {
        public Map<Character, Icon> items = new HashMap<>();
        public List<String> schema = new ArrayList<>();
        public Map<String, Object> options = new HashMap<>();
    }
}
