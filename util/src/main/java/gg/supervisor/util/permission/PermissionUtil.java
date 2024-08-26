package gg.supervisor.util.permission;

import java.util.HashSet;
import java.util.Set;

public class PermissionUtil<T extends Enum<T> & OctalValue> {

    private final Class<T> enumClass;

    public PermissionUtil(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    public int getOctalFromPermissions(Set<T> permissions) {
        int permissionValue = 0;

        for (T permission : permissions) {
            permissionValue += permission.getValue();
        }

        return permissionValue;
    }

    public boolean hasPermission(int octalValue, T permission) {
        return (octalValue & permission.getValue()) != 0;
    }

    public Set<T> getPermissionsFromOctal(int octal) {
        Set<T> permissions = new HashSet<>();
        for (T permission : enumClass.getEnumConstants()) {
            if ((octal & permission.getValue()) != 0) {
                permissions.add(permission);
            }
        }
        return permissions;
    }
}
