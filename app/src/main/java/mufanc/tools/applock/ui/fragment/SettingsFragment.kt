package mufanc.tools.applock.ui.fragment

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.TwoStatePreference
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mufanc.easyhook.api.catch
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.ViewLicenseDialogBinding
import mufanc.tools.applock.ui.adapter.LicenseListAdapter
import mufanc.tools.applock.util.Globals
import mufanc.tools.applock.util.Settings
import mufanc.tools.applock.util.SettingsAdapter
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

class SettingsFragment : SettingsAdapter.SettingsFragment() {

    private lateinit var backupLauncher: ActivityResultLauncher<String>
    private lateinit var restoreLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        backupLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
            if (uri == null) return@registerForActivityResult
            requireContext().contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(
                    Globals.LOCKED_APPS.joinToString("\n")
                    .toByteArray(StandardCharsets.UTF_8))
            }
        }

        restoreLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
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

    private abstract class Holder<T : Preference>(val pref: T) {
        abstract fun onChange(value: Any)
        init {
            pref.setOnPreferenceChangeListener { _, value ->
                onChange(value)
                true
            }
        }
    }

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        super.onCreatePreferences(bundle, rootKey)

        val hHideIcon = object : Holder<TwoStatePreference>(findPreference(Settings.HIDE_ICON.key)!!) {
            override fun onChange(value: Any) {
                requireContext().packageManager.setComponentEnabledSetting(
                    ComponentName(requireContext(), "${BuildConfig.APPLICATION_ID}.Launcher"),
                    if (value as Boolean) {
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    } else {
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    },
                    PackageManager.DONT_KILL_APP
                )
            }
        }

        val hWorkMode = object : Holder<ListPreference>(findPreference(Settings.WORK_MODE.key)!!) {
            override fun onChange(value: Any) {
                hHideIcon.pref.apply {
                    when (Settings.WorkMode.valueOf(value as String)) {
                        Settings.WorkMode.SHIZUKU -> {
                            isChecked = false
                            isEnabled = false
                            hHideIcon.onChange(false)
                        }
                        Settings.WorkMode.XPOSED -> {
                            isEnabled = true
                        }
                    }
                }
            }
        }

        hWorkMode.onChange(hWorkMode.pref.value)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {

        when (preference.key) {
            Settings.BACKUP_SCOPE.key -> {
                backupLauncher.launch("AppLock ${Date()}.txt")
            }
            Settings.RESTORE_SCOPE.key -> {
                restoreLauncher.launch(arrayOf("text/plain"))
            }
            Settings.AUTHOR.key -> {
                startActivity(Intent.parseUri(resources.getString(R.string.module_author_link), 0))
            }
            Settings.PROJECT_URL.key -> {
                startActivity(Intent.parseUri(resources.getString(R.string.project_url_summary), 0))
            }
            Settings.LICENSE.key -> {
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
}
