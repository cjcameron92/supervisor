package gg.supervisor.util.misc;

/**
 * A utility class that provides methods for retrieving class and method information,
 * including both names and actual Class objects, for the current and caller contexts.
 */
public class CallInfoUtil {

    /**
     * Gets the name of the class in which this method is called.
     * @return The name of the current class.
     */
    public static String getCurrentClassName() {
        return Thread.currentThread().getStackTrace()[2].getClassName();
    }

    /**
     * Gets the Class object of the class in which this method is called.
     * @return The Class object of the current class.
     * @throws ClassNotFoundException if the class cannot be located.
     */
    public static Class<?> getCurrentClass() throws ClassNotFoundException {
        String className = getCurrentClassName();
        return Class.forName(className);
    }

    /**
     * Gets the name of the method in which this method is called.
     * @return The name of the current method.
     */
    public static String getCurrentMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    /**
     * Gets the name of the class that called the method in which this is used.
     * @return The name of the caller class.
     */
    public static String getCallerClassName() {
        return Thread.currentThread().getStackTrace()[3].getClassName();
    }

    /**
     * Gets the Class object of the class that called the method in which this is used.
     * @return The Class object of the caller class.
     * @throws ClassNotFoundException if the class cannot be located.
     */
    public static Class<?> getCallerClass() throws ClassNotFoundException {
        String className = getCallerClassName();
        return Class.forName(className);
    }

    /**
     * Gets the name of the method that called the method in which this is used.
     * @return The name of the caller method.
     */
    public static String getCallerMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    /**
     * Returns the calling class and method name in a formatted string.
     * @return A formatted string with class and method information.
     */
    public static String getCurrentClassAndMethod() {
        return getCurrentClassName() + "#" + getCurrentMethodName();
    }

    /**
     * Returns the caller's class and method name in a formatted string.
     * @return A formatted string with caller class and method information.
     */
    public static String getCallerClassAndMethod() {
        return getCallerClassName() + "#" + getCallerMethodName();
    }
}
