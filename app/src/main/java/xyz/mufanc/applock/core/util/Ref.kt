package xyz.mufanc.applock.core.util

import org.joor.Reflect

class Ref(private val inner: Any?) {

    private val reflect = Reflect.on(inner)

    @Suppress("UNCHECKED_CAST")
    fun <T> obtain() = inner as T?

    operator fun get(key: String): Ref {
        return Ref(if (inner == null) null else reflect.get<Any>(key))
    }
}
