package mufanc.tools.applock.fragment.settings

import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mufanc.easyhook.util.catch
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.ViewLicenseDialogBinding
import mufanc.tools.applock.util.Globals
import mufanc.tools.applock.widget.MaterialListPreference
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var backupScope: ActivityResultLauncher<String>

    private lateinit var restoreScope: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backupScope = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
            if (uri == null) return@registerForActivityResult
            requireContext().contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(Globals.LOCKED_APPS.joinToString("\n")
                    .toByteArray(StandardCharsets.UTF_8))
            }
        }

        restoreScope = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@registerForActivityResult
            catch {
                requireContext().contentResolver.openInputStream(uri).use { stream ->
                    val scope = BufferedReader(InputStreamReader(stream)).readLines()
                        .map { it.trim() }.filter { it.isNotEmpty() }
                    if (scope.all { it.matches("^[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+$".toRegex()) }) {
                        Globals.LOCKED_APPS = scope.toMutableSet()
                    } else {
                        Toast.makeText(requireContext(), R.string.invalid_backup, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        findPreference<MaterialListPreference>("work_mode")?.let { mode ->
            val hideIcon = findPreference<SwitchPreferenceCompat>("hide_icon")
            fun listener(value: Any) {
                when (value) {
                    "shizuku" -> hideIcon?.apply {
                        isChecked = false
                        isEnabled = false
                    }
                    "xposed" -> hideIcon?.apply {
                        isEnabled = true
                    }
                }
            }
            listener(mode.value)
            mode.setOnPreferenceChangeListener { _, value ->
                listener(value)
                true
            }
        }
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
            "backup_scope" -> backupScope.launch("AppLock ${Date()}.txt")
            "restore_scope" -> restoreScope.launch(arrayOf("text/plain"))
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