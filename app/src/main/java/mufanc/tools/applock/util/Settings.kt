package mufanc.tools.applock.util

import androidx.annotation.Keep
import androidx.annotation.StringRes
import mufanc.tools.applock.MyApplication
import mufanc.tools.applock.R

@Keep
object Settings : SettingsAdapter() {

    enum class WorkMode(@StringRes override val summary: Int) : ListOptionItem {
        XPOSED(R.string.work_mode_xposed),
        SHIZUKU(R.string.work_mode_shizuku)
    }

    @Category(R.string.category_settings)
    val WORK_MODE = ListOption(WorkMode::class.java) {
        icon = R.drawable.ic_work_mode
        title = R.string.work_mode_title
    }

    @Category(R.string.category_settings)
    val HIDE_ICON = SwitchOption(false) {
        icon = R.drawable.ic_hide_icon
        title = R.string.hide_icon_title
        summary = R.string.hide_icon_summary
    }

    enum class ResolveMode(@StringRes override val summary: Int) : ListOptionItem {
        CATEGORY_LAUNCHER(R.string.resolve_mode_category_launcher),
        NON_SYSTEM_APPS(R.string.resolve_mode_non_system_apps),
        ALL_APPS(R.string.resolve_mode_all_apps)
    }

    @Category(R.string.category_settings)
    val RESOLVE_MODE = ListOption(ResolveMode::class.java) {
        icon = R.drawable.ic_resolve_mode
        title = R.string.resolve_mode_title
    }

    @Category(R.string.category_settings)
    val THEME_COLOR = Option {
        icon = R.drawable.ic_palette
        title = R.string.theme_color_title
        summary = R.string.theme_color_summary
    }

    @Category(R.string.category_backup_restore)
    val BACKUP_SCOPE = Option {
        icon = R.drawable.ic_backup_scope
        title = R.string.backup_scope_title
        summary = R.string.backup_scope_summary
    }

    @Category(R.string.category_backup_restore)
    val RESTORE_SCOPE = Option {
        icon = R.drawable.ic_restore_scope
        title = R.string.restore_scope_title
        summary = R.string.restore_scope_summary
    }

    @Category(R.string.category_about)
    val AUTHOR = Option {
        icon = R.drawable.ic_module_author
        title = R.string.module_author_title
        summary = R.string.module_author_summary
    }

    @Category(R.string.category_about)
    val PROJECT_URL = Option {
        icon = R.drawable.ic_project_url
        title = R.string.project_url_title
        summary = R.string.project_url_summary
    }

    @Category(R.string.category_about)
    val LICENSE = Option {
        icon = R.drawable.ic_license
        title = R.string.license_title
        summary = R.string.license_summary
    }

    init {
        initialize(MyApplication.context)
    }
}