package xyz.mufanc.applock.core.util

import android.app.ActivityThread
import android.content.Context

object ContextHelper {
    fun systemContext(): Context {
        return ActivityThread.currentActivityThread().systemContext
    }
}
