package xyz.mufanc.applock.util

import android.os.SystemProperties

object PropHelper {

    private const val MAX_AGE = 5000

    private val cache = HashMap<String, Pair<Any?, Long>>()  // key -> (value, last-updated)

    @Suppress("UNCHECKED_CAST")
    private fun <R> autoCache(key: String, func: () -> R): R {
        val pair = cache[key]
        val now = System.currentTimeMillis()

        val value = if (pair != null && now - pair.second < MAX_AGE) {
            pair.first as R
        } else {
            func()
        }

        cache[key] = Pair(value, now)

        return value
    }

    fun get(key: String): String? {
        return autoCache(key) { SystemProperties.get(key) }
    }

    fun getBoolean(key: String, def: Boolean): Boolean {
        return autoCache(key) { SystemProperties.getBoolean(key, def) }
    }
}
