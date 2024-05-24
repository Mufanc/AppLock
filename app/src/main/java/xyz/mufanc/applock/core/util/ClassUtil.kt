package xyz.mufanc.applock.core.util

import org.joor.Reflect
import sun.misc.Unsafe
import java.lang.reflect.Modifier

object ClassUtil {

    private val theUnsafe: Unsafe = Reflect.onClass(Unsafe::class.java).call("getUnsafe").get()

    private val sAccessFlags = Class::class.java.getDeclaredField("accessFlags")

    fun makePublic(klass: Class<*>) {
        val offset = theUnsafe.objectFieldOffset(sAccessFlags)
        val flags = theUnsafe.getInt(klass, offset)
        theUnsafe.putInt(klass, offset, flags or Modifier.PUBLIC)
    }

    fun makeNonFinal(klass: Class<*>) {
        val offset = theUnsafe.objectFieldOffset(sAccessFlags)
        val flags = theUnsafe.getInt(klass, offset)
        theUnsafe.putInt(klass, offset, flags and Modifier.FINAL.inv())
    }
}
