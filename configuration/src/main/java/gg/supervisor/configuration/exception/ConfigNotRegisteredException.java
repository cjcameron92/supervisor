package gg.supervisor.configuration.exception;

public class ConfigNotRegisteredException extends RuntimeException {

    public ConfigNotRegisteredException() {
        super("Config was not registered. Register it before loading or saving.");
    }
}
