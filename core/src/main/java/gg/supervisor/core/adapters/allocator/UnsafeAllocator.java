package gg.supervisor.core.adapters.allocator;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * UnsafeAllocator provides a mechanism to create instances of classes without calling their constructors.
 * This class utilizes the internal {@code sun.misc.Unsafe} API, which allows low-level memory operations.
 * Note: Using Unsafe can lead to unstable and unpredictable behavior and should be avoided in production code.
 */
public abstract class UnsafeAllocator {

    /**
     * Abstract method to create a new instance of the specified class type without invoking the constructor.
     *
     * @param <T> The type of the object to create.
     * @param c   The {@code Class} object representing the type to be instantiated.
     * @return A new instance of the specified class.
     * @throws Exception if there is an error during instance creation.
     */
    public abstract <T> T newInstance(Class<T> c) throws Exception;

    /**
     * Factory method to create an instance of UnsafeAllocator using {@code Unsafe.allocateInstance()}.
     *
     * @return An instance of {@code UnsafeAllocator}.
     * @throws UnsupportedOperationException if the {@code Unsafe} instance could not be accessed or if instantiation fails.
     */
    public static UnsafeAllocator create() {
        try {
            // Attempt to get an instance of Unsafe
            final Unsafe unsafe = getUnsafe();
            // Use reflection to access the allocateInstance method of Unsafe
            final Method allocateInstance = Unsafe.class.getMethod("allocateInstance", Class.class);

            // Return a new instance of UnsafeAllocator, utilizing Unsafe's allocateInstance method
            return new UnsafeAllocator() {
                @SuppressWarnings("unchecked")
                @Override
                public <T> T newInstance(Class<T> c) throws Exception {
                    return (T) allocateInstance.invoke(unsafe, c);
                }
            };
        } catch (Exception ignored) {
            // Ignoring exception to throw a more generic UnsupportedOperationException later
        }

        // If the Unsafe instance or method invocation fails, throw an exception indicating the failure
        throw new UnsupportedOperationException("Could not create an instance of UnsafeAllocator");
    }

    /**
     * Helper method to access the {@code Unsafe} instance.
     * This method uses reflection to bypass JVM security checks to access the {@code theUnsafe} field.
     *
     * @return The {@code Unsafe} instance.
     * @throws RuntimeException if the {@code Unsafe} instance could not be accessed.
     */
    private static Unsafe getUnsafe() {
        try {
            // Access the private static field "theUnsafe" in the Unsafe class
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);  // Bypass security checks to access the field
            return (Unsafe) f.get(null);  // Retrieve the Unsafe instance
        } catch (Exception e) {
            // If accessing Unsafe fails, throw a runtime exception with the cause
            throw new RuntimeException("Unable to access Unsafe", e);
        }
    }
}
