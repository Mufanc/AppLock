package xyz.mufanc.applock.ui.widgets

import android.app.Activity
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.mufanc.applock.App
import xyz.mufanc.applock.databinding.DialogThemeColorSelectorBinding
import xyz.mufanc.applock.databinding.ItemDialogThemeColorListBinding
import xyz.mufanc.applock.ui.util.ThemeManager
import xyz.mufanc.applock.util.Configs
import xyz.mufanc.applock.util.I18n

class ThemeColorSelectorDialog private constructor(
    private val dialog: AlertDialog
) : DialogInterface by dialog {

    companion object {
        operator fun invoke(activity: Activity): ThemeColorSelectorDialog {
            val dialog = MaterialAlertDialogBuilder(activity)
                .setTitle(I18n.strings.dialogThemeColorSelectorTitle)
                .setPositiveButton(I18n.strings.dialogThemeColorSelectorDismiss) { _, _ -> }
                .create()
                .apply {
                    val binding = DialogThemeColorSelectorBinding.inflate(layoutInflater)
                    setView(binding.root)
                    binding.themeColors.run {
                        layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
                        adapter = Adapter(this@apply, activity)
                    }
                }

            return ThemeColorSelectorDialog(dialog)
        }
    }

    fun show() {
        dialog.show()
    }

    private class Adapter(
        private val dialog: AlertDialog,
        private val activity: Activity
    ) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        class ViewHolder(
            binding: ItemDialogThemeColorListBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            val icon = binding.icon
        }

        override fun getItemCount(): Int {
            val count = ThemeManager.ThemeColor.entries.size
            return count - if (Configs.isMonetEnabled) 0 else 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemDialogThemeColorListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val theme = ThemeManager.ThemeColor.entries[position]

            holder.icon.setImageResource(theme.icon)
            holder.icon.setOnClickListener {

                App.prefs
                    .edit()
                    .apply {
                        putString(ThemeManager.PREFERENCE_KEY, theme.name)
                    }
                    .apply()

                dialog.dismiss()
                activity.recreate()
            }
        }
    }
}
