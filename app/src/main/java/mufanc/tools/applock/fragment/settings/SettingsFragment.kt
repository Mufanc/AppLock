package mufanc.tools.applock.fragment.settings

import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.ViewLicenseDialogBinding

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "project_url" -> {
                startActivity(Intent.parseUri(resources.getString(R.string.project_url_summary), 0))
            }
            "license" -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getText(R.string.license_title))
                    .setPositiveButton(resources.getText(R.string.dismiss)) { _, _ -> }
                    .create()
                    .apply {
                        val binding = ViewLicenseDialogBinding.inflate(layoutInflater)
                        setView(binding.root)
                        binding.licenseList.apply {
                            layoutManager = LinearLayoutManager(
                                context, LinearLayoutManager.VERTICAL, false
                            )
                            adapter = LicenseListAdapter(resources.getStringArray(R.array.license))
                        }
                    }
                    .show()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        when (key) {
            "hide_icon" -> {
                requireContext().packageManager.setComponentEnabledSetting(
                    ComponentName(requireContext(), "${BuildConfig.APPLICATION_ID}.Launcher"),
                    if (prefs.getBoolean(key, false)) {
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    } else {
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    },
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }
}