package gg.supervisor.menu.guis.builder;

import java.util.ArrayList;
import java.util.List;

public class SchemaBuilder {

    private final List<String> schema = new ArrayList<>();

    public SchemaBuilder add(String row) {
        schema.add(row);

        return this;
    }

    public SchemaBuilder add(String... row) {
        schema.addAll(List.of(row));

        return this;
    }

    public SchemaBuilder add(String row, int times) {

        for (int i = 0; i < times; i++) {
            schema.add(row);
        }

        return this;
    }

    public SchemaBuilder addEmpty() {
        schema.add("");

        return this;
    }

    public SchemaBuilder addEmpty(int times) {
        for (int i = 0; i < times; i++) {
            addEmpty();
        }

        return this;
    }

    public SchemaBuilder clear() {
        schema.clear();

        return this;
    }

    public String[] build() {
        return schema.subList(0, Math.min(schema.size(), 6)).toArray(new String[0]);
    }

}
