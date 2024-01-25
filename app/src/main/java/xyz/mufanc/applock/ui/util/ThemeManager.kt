package xyz.mufanc.applock.ui.util

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StyleRes
import xyz.mufanc.applock.App
import xyz.mufanc.applock.R
import xyz.mufanc.applock.util.Configs

object ThemeManager {

    val PREFERENCE_KEY by lazy {
        App.instance.getString(R.string.misc_theme_color_preference_key)
    }

    @Keep
    enum class ThemeColor(@DrawableRes val icon: Int) {
        PYRO(R.drawable.ic_element_pyro),
        HYDRO(R.drawable.ic_element_hydro),
        ANEMO(R.drawable.ic_element_anemo),
        ELECTRO(R.drawable.ic_element_electro),
        DENDRO(R.drawable.ic_element_dendro),
        CRYO(R.drawable.ic_element_cryo),
        GEO(R.drawable.ic_element_geo),
        DYNAMIC(R.drawable.ic_element_dynamic)
    }

    @StyleRes
    fun getColorThemeStyle(): Int {
        val default = if (Configs.isMonetEnabled) ThemeColor.DYNAMIC.name else ThemeColor.PYRO.name
        val value = App.prefs.getString(PREFERENCE_KEY, default)!!

        return when (ThemeColor.valueOf(value)) {
            ThemeColor.PYRO -> R.style.Theme_AppLock_NatureElement_Pyro
            ThemeColor.HYDRO -> R.style.Theme_AppLock_NatureElement_Hydro
            ThemeColor.ANEMO -> R.style.Theme_AppLock_NatureElement_Anemo
            ThemeColor.ELECTRO -> R.style.Theme_AppLock_NatureElement_Electro
            ThemeColor.DENDRO -> R.style.Theme_AppLock_NatureElement_Dendro
            ThemeColor.CRYO -> R.style.Theme_AppLock_NatureElement_Cyro
            ThemeColor.GEO -> R.style.Theme_AppLock_NatureElement_Geo
            ThemeColor.DYNAMIC -> R.style.Theme_AppLock_NatureElement_Dynamic
        }
    }
}
