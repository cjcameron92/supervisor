package gg.supervisor.util.permission.template;


import gg.supervisor.util.permission.PermissionManager;
import lombok.Getter;

@Getter
public enum Permission {
    CHEST_ACCESS,
    PLACE_BREAK,
    BREAK_BLOCK,
    FLY;


    public static final @Getter PermissionManager<Permission> permissionManager = new PermissionManager<>(Permission.class);

    public boolean has(int permissionValue) {
        return permissionManager.hasPermission(permissionValue, this);
    }
}