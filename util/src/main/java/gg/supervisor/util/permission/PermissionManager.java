package gg.supervisor.util.permission;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Class to manage permissions using an enum type.
 * The enum class provided must implement enum type Enum<T>.
 * The permissions are represented as bit flags.
 * The class provides methods to convert permissions to octal format,
 * check if a specific permission is set in an octal value,
 * and retrieve permissions from an octal value.
 */
public class PermissionManager<T extends Enum<T>> {

    /**
     * Represents the enum class used for managing permissions with bit flags.
     * The provided class must implement the enum type Enum<T>.
     * Permissions are treated as bit flags and used to manipulate permission values.
     */
    private final Class<T> enumClass;

    /**
     * Constructor for PermissionManager class.
     *
     * @param enumClass The enum class used for managing permissions with bit flags.
     */
    public PermissionManager(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    /**
     * Calculates the octal representation of the combined permission values in the provided set.
     *
     * @param permissions A set of permissions to calculate the octal representation for.
     * @return The octal representation of the combined permission values in the set.
     */
    public int getOctalFromPermissions(Set<T> permissions) {
        int permissionValue = 0;

        for (T permission : permissions) {
            permissionValue += getOrdinalValue(permission);
        }

        return permissionValue;
    }

    /**
     * Checks if a specific permission is set in the given octal value.
     *
     * @param octalValue The octal value representing the permission flags.
     * @param permission The permission to check for in the octal value.
     * @return True if the permission is set in the octal value, false otherwise.
     */
    public boolean hasPermission(int octalValue, T permission) {
        return (octalValue & getOrdinalValue(permission)) != 0;
    }

    /**
     * Retrieves the permissions represented by the given octal value.
     *
     * @param octal The octal value representing permissions as bit flags.
     * @return A Set containing the permissions derived from the octal value.
     */
    public Set<T> getPermissionsFromOctal(int octal) {
        Set<T> permissions = new HashSet<>();
        for (T permission : enumClass.getEnumConstants()) {
            if ((octal & getOrdinalValue(permission)) != 0) {
                permissions.add(permission);
            }
        }
        return permissions;
    }

    /**
     * Retrieves the ordinal value of the given permission.
     *
     * @param permission the permission for which to retrieve the ordinal value
     * @return the calculated ordinal value based on the permission's ordinal position
     */
    private int getOrdinalValue(T permission) {
        return 1 << permission.ordinal();
    }

}
