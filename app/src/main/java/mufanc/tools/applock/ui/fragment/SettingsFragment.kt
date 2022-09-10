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
import mufanc.easyhook.api.Logger
import mufanc.easyhook.api.catch
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.R
import mufanc.tools.applock.core.xposed.AppLockService
import mufanc.tools.applock.databinding.ItemLicenseDialogBinding
import mufanc.tools.applock.databinding.ItemThemeColorDialogBinding
import mufanc.tools.applock.ui.adapter.LicenseListAdapter
import mufanc.tools.applock.ui.adapter.ThemeColorAdapter
import mufanc.tools.applock.util.ScopeManager
import mufanc.tools.applock.util.Settings
import mufanc.tools.applock.util.channel.Configs
import mufanc.tools.applock.util.settings.SettingsBuilder
import mufanc.tools.applock.util.update
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : SettingsBuilder.Fragment(Settings) {

    private lateinit var backupLauncher: ActivityResultLauncher<String>
    private lateinit var restoreLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        backupLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
            if (uri == null) return@registerForActivityResult
            requireContext().contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(Settings.backupToJson().toByteArray())
            }
            Logger.i("@Module: backup settings!")
        }

        restoreLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@registerForActivityResult
            catch {
                requireContext().contentResolver.openInputStream(uri).use { stream ->
                    try {
                        val scope = Settings.restoreFromJson(BufferedReader(InputStreamReader(stream)).readText())
                        ScopeManager.scope.update(scope)
                        ScopeManager.commit()
                    } catch (err: Throwable) {
                        Logger.e("@Module: failed to restore settings!", err = err)
                        Toast.makeText(requireContext(), R.string.invalid_backup, Toast.LENGTH_SHORT).show()
                    }
                    requireActivity().recreate()
                    Logger.i("@Module: restore settings")
                }
            }
        }
    }

    lateinit var onWorkModeChangedListener: (Settings.WorkMode) -> Unit

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        super.onCreatePreferences(bundle, rootKey)
        onWorkModeChangedListener.invoke(Settings.WORK_MODE.value)
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

            val killLevel = ListOption(
                mirror = Settings.KILL_LEVEL,
                icon = R.drawable.ic_kill_level,
                title = R.string.kill_level_title
            )

            killLevel.registerOnChangeListener {
                view?.post {
                    AppLockService.client?.updateConfigs(Configs.collect().asBundle())
                }
            }

            onWorkModeChangedListener = fun (mode) {
                when (mode) {
                    Settings.WorkMode.XPOSED -> {
                        hideIcon.preference.isEnabled = true
                        killLevel.preference.isEnabled = true
                    }
                    Settings.WorkMode.SHIZUKU -> {
                        hideIcon.preference.isEnabled = false
                        hideIcon.preference.isChecked = false
                        killLevel.preference.isEnabled = false
                    }
                }
            }

            workMode.registerOnChangeListener {
                onWorkModeChangedListener(it)
            }

            ListOption(
                mirror = Settings.RESOLVE_MODE,
                icon = R.drawable.ic_resolve_mode,
                title = R.string.resolve_mode_title
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
                icon = R.drawable.ic_backup_settings,
                title = R.string.backup_settings_title,
                summary = R.string.backup_settings_summary
            ).registerOnClickListener {
                val now = SimpleDateFormat("yyyy-MM-dd'T'HH:MM:SS", Locale.getDefault()).format(Date())
                backupLauncher.launch("AppLock_${now}.txt")
            }

            Option(
                icon = R.drawable.ic_restore_scope,
                title = R.string.restore_settings_title,
                summary = R.string.restore_settings_summary
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
