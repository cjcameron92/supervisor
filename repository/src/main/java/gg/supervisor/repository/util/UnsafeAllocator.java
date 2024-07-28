package gg.supervisor.repository.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class UnsafeAllocator {
    public abstract <T> T newInstance(Class<T> c) throws Exception;

    public static UnsafeAllocator create() {
        // try the Unsafe.allocateInstance() method
        try {
            final Unsafe unsafe = getUnsafe();
            final Method allocateInstance = Unsafe.class.getMethod("allocateInstance", Class.class);
            return new UnsafeAllocator() {
                @SuppressWarnings("unchecked")
                @Override
                public <T> T newInstance(Class<T> c) throws Exception {
                    return (T) allocateInstance.invoke(unsafe, c);
                }
            };
        } catch (Exception ignored) {
        }

        throw new UnsupportedOperationException("Could not create an instance of UnsafeAllocator");
    }

    private static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to access Unsafe", e);
        }
    }
}
