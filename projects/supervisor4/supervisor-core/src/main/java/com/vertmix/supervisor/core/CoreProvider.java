package com.vertmix.supervisor.core;

import java.nio.file.Path;

public class CoreProvider<T> {

    private final Path path;
    private final T source;

    public CoreProvider(Path path, T source) {
        this.path = path;
        this.source = source;
    }

    private boolean debug = false;

    public Path getPath() {
        return path;
    }

    public T getSource() {
        return source;
    }

    public void log(String str) {

    }

    public void debug(String str) {

    }

    public void error(String str) {

    }
}
