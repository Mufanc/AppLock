package mufanc.tools.applock.util.settings

import android.content.Context
import androidx.preference.Preference
import androidx.preference.PreferenceGroup

sealed interface BaseOption {
    interface OptionGroup : BaseOption, Renderable<PreferenceGroup>
    sealed class Option<T, P : Preference>(
        mirror: SettingsBuilder.Holder<T>.Proxy?,
        private val icon: Int?, private val title: Int?, private val summary: Int?
    ) : BaseOption, Renderable<Preference> {

        lateinit var preference: P
        private val key = mirror?.key ?: nextKey()

        fun registerOnClickListener(callback: () -> Unit) {
            preference.setOnPreferenceClickListener {
                callback()
                true
            }
        }

        protected abstract fun parseValue(value: Any): T

        fun registerOnChangeListener(callback: (T) -> Unit) {
            preference.setOnPreferenceChangeListener { _, value ->
                callback(parseValue(value))
                true
            }
        }

        final override fun create(context: Context): Preference {
            preference.key = key
            icon?.let { preference.setIcon(it) }
            title?.let { preference.setTitle(it) }
            summary?.let { preference.setSummary(it) }
            return preference
        }

        final override fun render(target: Preference) {
            throw UnsupportedOperationException("Please use `create(preload: Context)` instead!")
        }

        companion object {
            private var count: Int = 0
            @Synchronized private fun nextKey() = "_Key${count++}"
        }
    }
}
