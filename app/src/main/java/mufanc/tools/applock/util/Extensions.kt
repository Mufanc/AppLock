package mufanc.tools.applock.util

import android.os.IBinder
import android.os.Parcel
import java.lang.reflect.Method

private val DEBUG_PID_TRANSACTION = "_PID"
    .reversed()
    .toByteArray()
    .mapIndexed { i, ch -> ch.toInt() shl (i * 8) }
    .sum()

fun IBinder.getRemotePid(): Int {
    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    try {
        this.transact(DEBUG_PID_TRANSACTION, data, reply, 0)
        return reply.readInt()
    } finally {
        data.recycle()
        reply.recycle()
    }
}

fun <T> MutableCollection<T>.update(elements: Collection<T>) {
    synchronized (this) {
        this.clear()
        this.addAll(elements)
    }
}

fun Method.signature(): String {
    fun typeToString(type: Class<*>): String {
        return java.lang.reflect.Array.newInstance(type, 0)
            .javaClass.name.replace('.', '/').substring(1)
    }

    val builder = StringBuilder("(")
    this.parameterTypes.forEach {
        builder.append(typeToString(it))
    }
    builder.append(")")
    builder.append(if (returnType == Void.TYPE) "V" else typeToString(returnType))
    return builder.toString()
}
