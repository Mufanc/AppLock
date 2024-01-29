package xyz.mufanc.applock.ui.fragment.apps

import android.content.pm.ApplicationInfo
import android.util.ArraySet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import xyz.mufanc.applock.databinding.ItemFragmentAppsAppListBinding

class AppListAdapter(
    private val lifecycle: LifecycleOwner,
    private val props: AppsViewModel
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemFragmentAppsAppListBinding) : RecyclerView.ViewHolder(binding.root) {
        val card = binding.appInfo
    }

    private val cache = mutableListOf<ApplicationInfo>()
    private val scope = ArraySet<String>()

    override fun getItemCount(): Int = cache.size

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        return ViewHolder(
            ItemFragmentAppsAppListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = cache[position]
        holder.card.info = info
        holder.card.inScope = scope.contains(info.packageName)
    }

    init {
        props.apps.observe(lifecycle) { list ->
            cache.clear()
            cache.addAll(list)
        }
    }
}
