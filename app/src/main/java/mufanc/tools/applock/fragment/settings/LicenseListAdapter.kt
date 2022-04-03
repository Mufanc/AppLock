package mufanc.tools.applock.fragment.settings

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.ViewLicenseItemBinding

class LicenseListAdapter(
    private val licenseList: Array<String>
) : RecyclerView.Adapter<LicenseListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val projectName: TextView
        val licenseType: TextView

        init {
             ViewLicenseItemBinding.bind(view).let {
                projectName = it.projectName
                licenseType = it.licenseType
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater
            .from(parent.context)
            .inflate(R.layout.view_license_item, parent, false)
            .let { ViewHolder(it) }
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