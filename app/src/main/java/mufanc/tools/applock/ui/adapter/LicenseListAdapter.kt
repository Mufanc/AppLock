package mufanc.tools.applock.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mufanc.tools.applock.databinding.ItemLicenseBinding

class LicenseListAdapter(
    private val licenseList: Array<String>
) : RecyclerView.Adapter<LicenseListAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemLicenseBinding) : RecyclerView.ViewHolder(binding.root) {
        val projectName: TextView
        val licenseType: TextView

        init {
            projectName = binding.projectName
            licenseType = binding.licenseType
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLicenseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val licenseInfo = licenseList[position].split("|")
        holder.apply {
            projectName.text = licenseInfo[0].trim()
            licenseType.text = licenseInfo[1].trim()
            itemView.setOnClickListener {
                itemView.context.startActivity(Intent.parseUri(licenseInfo[2].trim(), 0))
            }
        }
    }

    override fun getItemCount() = licenseList.size
}
