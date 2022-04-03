package mufanc.tools.applock.fragment.apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.ViewAppsItemBinding
import java.text.Collator
import java.util.*

class AppListAdapter(
   private val appList: List<AppsViewModel.AppInfo>
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>(), Filterable {

    private val filteredList = mutableListOf<AppsViewModel.AppInfo>()

    init {
        filter.filter("")
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appIcon: ImageView
        val appName: TextView
        val packageName: TextView
        val locked: CheckBox

        init {
            ViewAppsItemBinding.bind(view).let {
                appIcon = it.appIcon
                appName = it.appName
                packageName = it.packageName
                locked = it.locked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater
            .from(parent.context)
            .inflate(R.layout.view_apps_item, parent, false)
            .let { ViewHolder(it) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = filteredList[position]
        holder.apply {
            appIcon.setImageDrawable(appInfo.icon)
            appName.text = appInfo.appName
            packageName.text = appInfo.packageName
        }
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence): FilterResults {
                return FilterResults().apply {
                    values = appList.filter {
                        it.appName.lowercase().contains(query.toString().lowercase())
                    }.sortedWith { o1, o2 ->
                        Collator.getInstance(Locale.getDefault()).compare(o1.appName, o2.appName)
                    }
                }
            }

            @Suppress("Unchecked_Cast")
            override fun publishResults(query: CharSequence?, results: FilterResults) {
                val newList = results.values as List<AppsViewModel.AppInfo>

                DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize() = filteredList.size

                    override fun getNewListSize() = newList.size

                    override fun areItemsTheSame(op: Int, np: Int) =
                        filteredList[op].packageName == newList[np].packageName

                    override fun areContentsTheSame(op: Int, np: Int) =
                        filteredList[op].packageName == newList[np].packageName
                }).let {
                    filteredList.clear()
                    filteredList.addAll(newList)
                    it.dispatchUpdatesTo(this@AppListAdapter)
                }
            }
        }
    }
}