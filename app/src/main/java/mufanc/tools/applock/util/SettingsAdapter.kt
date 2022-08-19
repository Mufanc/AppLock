package mufanc.tools.applock.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.preference.*
import mufanc.tools.applock.ui.widget.MaterialListPreference
import java.util.*

@Suppress("LeakingThis")
abstract class SettingsAdapter : SharedPreferences.OnSharedPreferenceChangeListener {

    // 单例检查
    companion object {
        private val innerField by lazy {
            BaseOption::class.java.getDeclaredField("inner").apply { isAccessible = true }
        }
        private var instance: SettingsAdapter? = null
    }

    init {
        instance?.let {
            throw RuntimeException("`SettingsAdapter` can only be instantiated once.")
        } ?: run {
            instance = this
        }
    }

    private val order = WeakHashMap<Any, Int>()
    private val record = mutableMapOf<String, BaseOption<*>>()  // 用于配置变更时通过 key 更新属性值

    // 设置构造器 & 组件
    @Target(AnnotationTarget.FIELD)
    annotation class Category(@StringRes val nameId: Int)

    abstract inner class BaseOption<T : Any> {
        @DrawableRes var icon: Int? = null
        @StringRes var title: Int? = null

        protected var inner: T? = null
        abstract val value: T
        lateinit var key: String

        init {
            order[this] = order.size
        }
    }

    private fun <T : Any>BaseOption<T>.assignInner(value: T) {
        innerField.set(this,  value)
    }

    inner class Option(initializer: Option.() -> Unit) : BaseOption<Nothing>() {

        @StringRes var summary: Int? = null
        override val value get() = throw RuntimeException("option type `Option` won't have a value!")

        init {
            initializer(this)
        }
    }

    inner class SwitchOption(
        private val default: Boolean, initializer: SwitchOption.() -> Unit
    ) : BaseOption<Boolean>() {

        @StringRes var summary: Int? = null
        override val value: Boolean get() = inner ?: default

        init {
            initializer(this)
        }
    }

    interface ListOptionItem { val summary: Int }
    inner class ListOption<T>(
        val source: Class<T>, initializer: ListOption<T>.() -> Unit
    ) : BaseOption<T>() where T : Enum<T>, T : ListOptionItem {

        override val value: T get() = inner ?: source.enumConstants!![0]

        init {
            initializer(this)
        }
    }

    // 监听设置变化
    private fun update(prefs: SharedPreferences, key: String, option: BaseOption<*>) {
        when (option) {
            is SwitchOption -> {
                option.assignInner(prefs.getBoolean(key, false))
            }
            is ListOption<*> -> {
                val value = prefs.getString(key, "")
                @Suppress("Unchecked_Cast")
                (option as BaseOption<Enum<*>>).assignInner(
                    option.source.enumConstants.find {
                        it.name == value
                    }!!
                )
            }
        }
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        update(prefs, key, record[key]!!)
    }

    // 子类需在初始化成员后调用此方法完成初始化
    fun initialize(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        javaClass.declaredFields.forEach { prop ->
            prop.isAccessible = true
            val key = prop.name

            prop.get(this).takeIf { order[it] != null }?.let { option ->
                option as BaseOption<*>
                option.key = key
                if (prefs.contains(key)) update(prefs, key, option)
                record[key] = option
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    // SettingsFragment 相关
    abstract class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
            // Todo: 现在必须在初始化 SettingsFragment 前实例化设置，这可能导致一些问题
            instance!!.render(this)
        }
    }

    private fun dispatchWidget(option: BaseOption<*>, context: Context): Preference {
        return when (option) {
            is Option -> Preference(context).apply {
                option.summary?.let { setSummary(it) }
            }
            is SwitchOption -> SwitchPreferenceCompat(context).apply {
                option.summary?.let { setSummary(it) }
            }
            is ListOption<*> -> MaterialListPreference(context).apply {
                val items = option.source.enumConstants!!
                entries = items.map {
                    it as ListOptionItem
                    context.resources.getString(it.summary)
                }.toTypedArray()
                entryValues = items.map { it.name }.toTypedArray()
            }
            else -> throw RuntimeException("WTF??")
        }.apply {
            option.icon?.let { setIcon(it) }
            option.title?.let { setTitle(it) }
        }
    }

    private fun render(fragment: PreferenceFragmentCompat) {
        val context = fragment.requireContext()
        val screen = fragment.preferenceManager.createPreferenceScreen(context)

        val categories = mutableMapOf<Int, PreferenceCategory>()
        javaClass.declaredFields.asSequence()
            .mapNotNull { prop ->
                prop.isAccessible = true
                order[prop.get(this)]?.let { Pair(it, prop) }
            }.sortedBy {
                it.first
            }.forEach { (_, prop) ->
                val option = prop.get(this) as BaseOption<*>
                val preference = dispatchWidget(option, context)
                preference.key = option.key

                prop.getAnnotationsByType(Category::class.java).takeIf {
                    it.isNotEmpty()
                }?.first()?.let { category ->  // 有 @Category 注解
                    categories[category.nameId] ?: PreferenceCategory(context).let {  // 若无此分类则创建
                        it.setTitle(category.nameId)
                        categories[category.nameId] = it
                        screen.addPreference(it)
                    }
                    categories[category.nameId]!!.addPreference(preference)
                } ?: run {  // 无 @Category 注解
                    screen.addPreference(preference)
                }
            }

        fragment.preferenceScreen = screen
    }
}