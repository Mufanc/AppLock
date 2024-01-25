package xyz.mufanc.applock.ui.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import androidx.preference.forEach
import xyz.mufanc.applock.R
import xyz.mufanc.applock.ui.util.ThemeManager
import xyz.mufanc.applock.ui.widgets.LicenseListDialog
import xyz.mufanc.applock.ui.widgets.ThemeColorSelectorDialog

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        cache: Bundle?
    ): View {
        return super.onCreateView(inflater, container, cache).apply {
            setDivider(null)
        }
    }

    override fun onCreatePreferences(cache: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        preferenceScreen.forEach { preference ->
            (preference as? PreferenceGroup)?.forEach {
                it.layoutResource = R.layout.component_preference_card
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference.key) {
            ThemeManager.PREFERENCE_KEY -> {
                ThemeColorSelectorDialog(requireActivity()).show()
                true
            }
            "license" -> {
                LicenseListDialog(requireActivity()).show()
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }
}
