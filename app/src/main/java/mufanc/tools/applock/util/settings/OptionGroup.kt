package mufanc.tools.applock.util.settings

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import mufanc.tools.applock.ui.widget.MaterialListPreference
import mufanc.tools.applock.ui.widget.MaterialPreference
import mufanc.tools.applock.ui.widget.MaterialSwitchPreference

abstract class OptionGroup(
    context: Context
) : PreferenceGroup(context, null), BaseOption.OptionGroup {

    protected val children = mutableListOf<BaseOption>()

    private fun initialize(option: BaseOption) {
        children.add(option)
    }

    inner class Option(
        @DrawableRes icon: Int? = null,
        @StringRes title: Int? = null,
        @StringRes summary: Int? = null
    ) : BaseOption.Option<Nothing?, Preference>(null, icon, title, summary) {
        init {
            initialize(this)
            preference = MaterialPreference(context)
        }

        override fun parseValue(value: Any): Nothing? = null
    }

    inner class ListOption<T>(
        private val mirror: SettingsBuilder.Holder<T>.Proxy,
        @DrawableRes icon: Int? = null,
        @StringRes title: Int? = null
    ) : BaseOption.Option<T, MaterialListPreference>(mirror, icon, title, null)
            where T : Enum<T>, T : SettingsBuilder.SourceList {

        init {
            initialize(this)
            preference = MaterialListPreference(context).apply {
                val items = (mirror.value.javaClass).enumConstants!!
                entries = items.map { context.resources.getString(it.summary) }.toTypedArray()
                entryValues = items.map { it.name }.toTypedArray()
            }
        }

        private val holder by lazy {
            val mirrorClass = mirror.javaClass
            mirrorClass.declaredFields.find {
                it.type == mirrorClass.enclosingClass
            }!!.run {
                isAccessible = true
                @Suppress("Unchecked_Cast")
                get(mirror) as SettingsBuilder.Holder.Enum<T>
            }
        }

        override fun parseValue(value: Any): T {
            return holder.valueOf(value as String)
        }
    }

    inner class SwitchOption(
        private val mirror: SettingsBuilder.Holder<Boolean>.Proxy,
        @DrawableRes icon: Int? = null,
        @StringRes title: Int? = null,
        @StringRes summary: Int? = null
    ) : BaseOption.Option<Boolean, MaterialSwitchPreference>(mirror, icon, title, summary) {
        init {
            initialize(this)
            preference = MaterialSwitchPreference(context).apply{
                isChecked = mirror.value
            }
        }

        override fun parseValue(value: Any) = value as Boolean
    }

    override fun create(context: Context): PreferenceGroup {
        throw UnsupportedOperationException("Please use `render(preload: T): Unit` instead!")
    }
}
