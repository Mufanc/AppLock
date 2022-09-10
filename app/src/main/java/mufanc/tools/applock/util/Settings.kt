package mufanc.tools.applock.util

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import mufanc.easyhook.api.Logger
import mufanc.tools.applock.R
import mufanc.tools.applock.ui.adapter.ThemeColorAdapter
import mufanc.tools.applock.util.settings.SettingsBuilder
import java.io.StringReader

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

    val SCOPE by Holder.StringSet(setOf())

    private val ThemeColorKey = ThemeColorAdapter.ThemeColor::class.java.simpleName

    fun backupToJson(): String {
        return JsonObject().apply {
            addProperty(WORK_MODE.key, WORK_MODE.value.name)
            addProperty(HIDE_ICON.key, HIDE_ICON.value)
            addProperty(RESOLVE_MODE.key, RESOLVE_MODE.value.name)
            addProperty(KILL_LEVEL.key, KILL_LEVEL.value.name)
            addProperty(
                ThemeColorKey,
                prefs.getString(ThemeColorKey, null)
            )
            add(
                SCOPE.key, JsonArray().also { arr ->
                    SCOPE.value.forEach(arr::add)
                }
            )
        }.toString()
    }

    fun restoreFromJson(json: String): Set<String> {
        val reader = JsonReader(StringReader(json))
        val scope = mutableSetOf<String>()

        reader.beginObject()
        prefs.edit().apply {
            while (reader.hasNext()) {
                when (val key = reader.nextName()) {
                    WORK_MODE.key -> putString(key, reader.nextString())
                    HIDE_ICON.key -> putBoolean(key, reader.nextBoolean())
                    RESOLVE_MODE.key -> putString(key, reader.nextString())
                    KILL_LEVEL.key -> putString(key, reader.nextString())
                    ThemeColorKey -> putString(key, reader.nextString())
                    SCOPE.key -> {
                        reader.beginArray()
                        while (reader.hasNext()) {
                            scope.add(reader.nextString())
                        }
                        putStringSet(key, scope)
                        reader.endArray()
                    }
                    else -> {
                        reader.skipValue()
                        Logger.w("@Module: Unknown key for settings: `$key`")
                    }
                }
            }
        }.apply()
        reader.endObject()

        return scope
    }

    init {
        register(WORK_MODE, HIDE_ICON, RESOLVE_MODE, KILL_LEVEL, SCOPE)
    }
}
