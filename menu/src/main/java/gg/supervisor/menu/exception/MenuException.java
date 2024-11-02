package gg.supervisor.menu.exception;

public final class MenuException extends RuntimeException {

    public MenuException(String message) {
        super(message);
    }

    public MenuException(String message, Exception cause) {
        super(message, cause);
    }
}
