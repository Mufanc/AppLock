package mufanc.tools.applock.util.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.preference.*
import kotlin.reflect.KProperty

abstract class SettingsBuilder : SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Settings Manager Part
     */

    private val keyToHolder = mutableMapOf<String, Holder<*>>()
    private var isDeviceProtectedStorage = false

    interface SourceList { val summary: Int }

    sealed class Holder<T>(private val default: T) {
        class Boolean(default: kotlin.Boolean) : Holder<kotlin.Boolean>(default)
        class Enum<T>(default: T) : Holder<T>(default) where T : kotlin.Enum<T>, T : SourceList {
            private val source: Class<T> = default.javaClass
            fun valueOf(value: String): T {
                return source.enumConstants!!.find {
                    it.name == value
                }!!
            }
        }

        var value: T? = null

        private lateinit var proxy: Proxy
        inner class Proxy(val key: String) {
            val value: T get() = this@Holder.value ?: default
        }

        operator fun <V : SettingsBuilder> getValue(builder: V, property: KProperty<*>): Proxy {
            if (::proxy.isInitialized.not()) {
                proxy = Proxy(property.name)
                builder.keyToHolder[property.name] = this
            }
            return proxy
        }
    }

    lateinit var sharedPrefs: SharedPreferences

    private fun updateHolder(holder: Holder<*>, key: String) {
        when (holder) {
            is Holder.Boolean -> holder.value = sharedPrefs.getBoolean(key, false)
            is Holder.Enum<*> -> {
                @Suppress("Unchecked_Cast")
                (holder as Holder<Enum<*>>).value = holder.valueOf(sharedPrefs.getString(key, "")!!)
            }
        }
    }

    @Deprecated("Don't call this method in the subclass")
    final override fun onSharedPreferenceChanged(ignored: SharedPreferences, key: String) {
        keyToHolder[key]?.let { holder ->
            updateHolder(holder, key)
        }
    }

    private lateinit var register: () -> Unit

    @Synchronized
    fun init(context: Context) {
        if (::sharedPrefs.isInitialized) return
        isDeviceProtectedStorage = context.isDeviceProtectedStorage
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefs.registerOnSharedPreferenceChangeListener(this)
        register.invoke()
    }

    protected fun register(vararg args: Holder<*>.Proxy) {
        register = {
            args.forEach { proxy ->
                if (sharedPrefs.contains(proxy.key)) {
                    updateHolder(keyToHolder[proxy.key]!!, proxy.key)
                }
            }
        }
    }

    /**
     * Fragment Part
     */

    abstract class Fragment(private val builder: SettingsBuilder) : PreferenceFragmentCompat() {

        inner class Screen : OptionGroup(requireContext()) {
            inner class Category(
                @StringRes private val category: Int,
                initializer: Category.() -> Unit
            ) : OptionGroup(context) {
                init {
                    this@Screen.children.add(this)
                    initializer(this)
                }

                override fun render(target: PreferenceGroup) {
                    target.setTitle(category)
                    children.forEach { option ->
                        target.addPreference((option as BaseOption.Option<*, *>).create(context))
                    }
                }
            }

            override fun render(target: PreferenceGroup) {
                children.forEach { option ->
                    when (option) {
                        is BaseOption.Option<*, *> -> {
                            target.addPreference(option.create(context))
                        }
                        is BaseOption.OptionGroup -> {
                            option.render(
                                PreferenceCategory(context).also { target.addPreference(it) }
                            )
                        }
                    }
                }
            }
        }

        protected fun buildFromDsl(initializer: Screen.() -> Unit): PreferenceScreen {
            val context = requireContext()
            val screen = preferenceManager.createPreferenceScreen(context)
            Screen().apply(initializer).render(screen)
            return screen
        }

        @CallSuper
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
            return super.onCreateView(inflater, container, bundle).apply {
                listView.removeItemDecorationAt(0)
            }
        }

        abstract fun buildScreen(): PreferenceScreen

        @CallSuper
        override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) = with (builder) {
            if (isDeviceProtectedStorage) {
                preferenceManager.setStorageDeviceProtected()
            }
            preferenceScreen = buildScreen()
        }
    }
}
