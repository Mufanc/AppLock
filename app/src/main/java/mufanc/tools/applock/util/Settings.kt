package mufanc.tools.applock.util

import androidx.annotation.Keep
import androidx.annotation.StringRes
import mufanc.tools.applock.R
import mufanc.tools.applock.util.settings.SettingsBuilder

@Keep
object Settings : SettingsBuilder() {

    enum class WorkMode(@StringRes override val summary: Int) : SourceList {
        XPOSED(R.string.work_mode_xposed),
        SHIZUKU(R.string.work_mode_shizuku)
    }

    val WORK_MODE by Holder.Enum(WorkMode.XPOSED)

    val HIDE_ICON by Holder.Boolean(false)

    enum class ResolveMode(@StringRes override val summary: Int) : SourceList {
        CATEGORY_LAUNCHER(R.string.resolve_mode_category_launcher),
        NON_SYSTEM_APPS(R.string.resolve_mode_non_system_apps),
        ALL_APPS(R.string.resolve_mode_all_apps)
    }

    val RESOLVE_MODE by Holder.Enum(ResolveMode.CATEGORY_LAUNCHER)

    enum class KillLevel(@StringRes override val summary: Int) : SourceList {
        TRIM_MEMORY(R.string.kill_level_trim_memory),
        NONE(R.string.kill_level_none)
    }

    val KILL_LEVEL by Holder.Enum(KillLevel.TRIM_MEMORY)

    init {
        register(WORK_MODE, HIDE_ICON, RESOLVE_MODE, KILL_LEVEL)
    }
}
