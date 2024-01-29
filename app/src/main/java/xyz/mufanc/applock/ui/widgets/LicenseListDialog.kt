package xyz.mufanc.applock.ui.widgets

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.mufanc.applock.R
import xyz.mufanc.applock.databinding.DialogLicenseListBinding
import xyz.mufanc.applock.databinding.ItemDialogLicenseListBinding
import xyz.mufanc.applock.util.I18n

class LicenseListDialog private constructor(
    private val dialog: AlertDialog
) : DialogInterface by dialog {

    companion object {
        operator fun invoke(activity: Activity): LicenseListDialog {
            val dialog = MaterialAlertDialogBuilder(activity)
                .setTitle(I18n.strings.dialogLicenseListTitle)
                .setPositiveButton(I18n.strings.dialogLicenseListDismiss) { _, _ -> }
                .create()
                .apply {
                    val binding = DialogLicenseListBinding.inflate(layoutInflater)
                    setView(binding.root)

                    binding.licenseList.run {
                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = Adapter(resources.getStringArray(R.array.license_list).toList())
                    }
                }

            return LicenseListDialog(dialog)
        }
    }

    fun show() {
        dialog.show()
    }

    private class Adapter(
        private val licenseList: List<String>
    ) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        class ViewHolder(binding: ItemDialogLicenseListBinding) : RecyclerView.ViewHolder(binding.root) {
            val projectName: TextView = binding.projectName
            val licenseName: TextView = binding.licenseName
        }

        override fun getItemCount(): Int = licenseList.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemDialogLicenseListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (projectName, licenseName, projectUrl) = licenseList[position]
                .split("|")
                .map { str -> str.trim() }

            holder.projectName.text = projectName
            holder.licenseName.text = licenseName

            holder.itemView.setOnClickListener { view ->
                view.context.startActivity(Intent.parseUri(projectUrl, 0))
            }
        }
    }
}
