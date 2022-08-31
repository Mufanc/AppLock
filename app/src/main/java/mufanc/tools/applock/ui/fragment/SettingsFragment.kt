package mufanc.tools.applock.ui.fragment

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mufanc.easyhook.api.catch
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.ItemLicenseDialogBinding
import mufanc.tools.applock.databinding.ItemThemeColorDialogBinding
import mufanc.tools.applock.ui.adapter.LicenseListAdapter
import mufanc.tools.applock.ui.adapter.ThemeColorAdapter
import mufanc.tools.applock.util.ScopeManager
import mufanc.tools.applock.util.Settings
import mufanc.tools.applock.util.settings.SettingsBuilder
import mufanc.tools.applock.util.update
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

class SettingsFragment : SettingsBuilder.Fragment(Settings) {

    private lateinit var backupLauncher: ActivityResultLauncher<String>
    private lateinit var restoreLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        backupLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
            if (uri == null) return@registerForActivityResult
            requireContext().contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(
                    ScopeManager.scope.joinToString("\n").toByteArray(StandardCharsets.UTF_8)
                )
            }
        }

        restoreLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@registerForActivityResult
            catch {
                requireContext().contentResolver.openInputStream(uri).use { stream ->
                    val scope = BufferedReader(InputStreamReader(stream)).readLines()
                        .map { it.trim() }.filter { it.isNotEmpty() }
                    if (scope.all { it.matches("^[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+$".toRegex()) }) {
                        ScopeManager.scope.update(scope)
                        ScopeManager.commit()
                    } else {
                        Toast.makeText(requireContext(), R.string.invalid_backup, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun buildScreen() = buildFromDsl {
        Category(R.string.category_settings) {
            val workMode = ListOption(
                mirror = Settings.WORK_MODE,
                icon = R.drawable.ic_work_mode,
                title = R.string.work_mode_title
            )

            val hideIcon = SwitchOption(
                mirror = Settings.HIDE_ICON,
                icon = R.drawable.ic_hide_icon,
                title = R.string.hide_icon_title,
                summary = R.string.hide_icon_summary
            )

            workMode.registerOnChangeListener { mode ->
                when (mode) {
                    Settings.WorkMode.XPOSED -> {
                        hideIcon.preference.isEnabled = true
                    }
                    Settings.WorkMode.SHIZUKU -> {
                        hideIcon.preference.isEnabled = false
                        hideIcon.preference.isChecked = false
                    }
                }
            }

            hideIcon.registerOnChangeListener { checked ->
                requireContext().packageManager.setComponentEnabledSetting(
                    ComponentName(requireContext(), "${BuildConfig.APPLICATION_ID}.Launcher"),
                    if (checked) {
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    } else {
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    },
                    PackageManager.DONT_KILL_APP
                )
            }

            ListOption(
                mirror = Settings.RESOLVE_MODE,
                icon = R.drawable.ic_resolve_mode,
                title = R.string.resolve_mode_title
            )

            ListOption(
                mirror = Settings.KILL_LEVEL,
                icon = R.drawable.ic_kill_level,
                title = R.string.kill_level_title
            )

            Option(
                icon = R.drawable.ic_palette,
                title = R.string.theme_color_title,
                summary = R.string.theme_color_summary
            ).registerOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.theme_color_title)
                    .setPositiveButton(resources.getText(R.string.dismiss)) { _, _ -> }
                    .create()
                    .apply {
                        val binding = ItemThemeColorDialogBinding.inflate(layoutInflater)
                        setView(binding.root)
                        binding.colors.let {
                            it.layoutManager = GridLayoutManager(
                                requireContext(), 4,
                                GridLayoutManager.VERTICAL, false
                            )
                            it.adapter = ThemeColorAdapter(requireActivity(), this)
                        }
                    }
                    .show()
            }
        }

        Category(R.string.category_backup_restore) {
            Option(
                icon = R.drawable.ic_backup_scope,
                title = R.string.backup_scope_title,
                summary = R.string.backup_scope_summary
            ).registerOnClickListener {
                backupLauncher.launch("AppLock ${Date()}.txt")
            }

            Option(
                icon = R.drawable.ic_restore_scope,
                title = R.string.restore_scope_title,
                summary = R.string.restore_scope_summary
            ).registerOnClickListener {
                restoreLauncher.launch(arrayOf("text/plain"))
            }
        }

        Category(R.string.category_about) {
            Option(
                icon = R.drawable.ic_module_author,
                title = R.string.module_author_title,
                summary = R.string.module_author_summary
            ).registerOnClickListener {
                startActivity(Intent.parseUri(resources.getString(R.string.module_author_link), 0))
            }

            Option(
                icon = R.drawable.ic_project_url,
                title = R.string.project_url_title,
                summary = R.string.project_url_summary
            ).registerOnClickListener {
                startActivity(Intent.parseUri(resources.getString(R.string.project_url_summary), 0))
            }

            Option(
                icon = R.drawable.ic_license,
                title = R.string.license_title,
                summary = R.string.license_summary
            ).registerOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getText(R.string.license_title))
                    .setPositiveButton(resources.getText(R.string.dismiss)) { _, _ -> }
                    .create()
                    .apply {
                        val binding = ItemLicenseDialogBinding.inflate(layoutInflater)
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
    }
}
