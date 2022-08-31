package mufanc.tools.applock.util.settings

import android.content.Context

interface Renderable<T> {
    fun create(context: Context) : T
    fun render(target: T)
}
