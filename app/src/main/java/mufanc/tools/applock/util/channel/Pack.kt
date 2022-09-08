package mufanc.tools.applock.util.channel

import android.os.Bundle
import kotlin.reflect.KProperty

abstract class Pack(protected val bundle: Bundle) {

    fun asBundle() = bundle

    protected operator fun <T, R> Bundle.getValue(ignored: T, property: KProperty<*>): R {
        @Suppress("Unchecked_Cast")
        return get(property.name) as R
    }
}
