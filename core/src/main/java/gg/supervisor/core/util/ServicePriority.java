package gg.supervisor.core.util;

public enum ServicePriority {

    HIGHEST(-2),
    HIGH(-1),
    NORMAL(0),
    LOW(1),
    LOWEST(2);

    private final int priority;

    ServicePriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }
}
