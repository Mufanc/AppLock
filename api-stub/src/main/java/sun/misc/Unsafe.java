package sun.misc;

import java.lang.reflect.Field;

public class Unsafe {
    public static Unsafe getUnsafe() {
        throw new RuntimeException("STUB");
    }

    public long objectFieldOffset(Field field) {
        throw new RuntimeException("STUB");
    }

    public native int getInt(Object obj, long offset);

    public native void putInt(Object obj, long offset, int value);
}
