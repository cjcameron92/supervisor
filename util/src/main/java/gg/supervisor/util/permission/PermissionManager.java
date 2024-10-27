package gg.supervisor.util.permission;

import java.util.HashSet;
import java.util.Set;

public class PermissionManager<T extends Enum<T>> {

    private final Class<T> enumClass;

    public PermissionManager(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    public int getOctalFromPermissions(Set<T> permissions) {
        int permissionValue = 0;

        for (T permission : permissions) {
            permissionValue += getOrdinalValue(permission);
        }

        return permissionValue;
    }

    public boolean hasPermission(int octalValue, T permission) {
        return (octalValue & getOrdinalValue(permission)) != 0;
    }

    public Set<T> getPermissionsFromOctal(int octal) {
        Set<T> permissions = new HashSet<>();
        for (T permission : enumClass.getEnumConstants()) {
            if ((octal & getOrdinalValue(permission)) != 0) {
                permissions.add(permission);
            }
        }
        return permissions;
    }

    private int getOrdinalValue(T permission) {
        return 1 << permission.ordinal();
    }

}
