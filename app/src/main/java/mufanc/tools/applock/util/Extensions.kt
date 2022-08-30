package mufanc.tools.applock.util

import android.os.IBinder
import android.os.Parcel

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

fun <T>MutableCollection<T>.update(elements: Collection<T>) {
    synchronized (this) {
        this.clear()
        this.addAll(elements)
    }
}
