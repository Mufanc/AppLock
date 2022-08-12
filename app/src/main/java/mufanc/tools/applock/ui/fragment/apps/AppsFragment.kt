package mufanc.tools.applock.ui.fragment.apps

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.FragmentAppsBinding
import mufanc.tools.applock.util.Globals

class AppsFragment : Fragment() {

    private var binding: FragmentAppsBinding? = null

    private var adapter: AppListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentAppsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val model = ViewModelProvider(this)[AppsViewModel::class.java]
        model.loadAppList(requireActivity()) {
            binding?.apply {
                progress.visibility = View.GONE
                applist.apply {
                    layoutManager = LinearLayoutManager(
                        context, LinearLayoutManager.VERTICAL, false
                    )
                    adapter = AppListAdapter(
                        model.appList,
                        model.lockedAppList
                    ).also { this@AppsFragment.adapter = it }
                }
            }
        }
        binding?.apply {
            refresh.setOnRefreshListener {
                model.loadAppList(requireActivity(), true) {
                    adapter?.filter?.filter("")
                    refresh.isRefreshing = false
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_apps_menu, menu)

        menu.findItem(R.id.search_app).actionView
            .let { it as SearchView }
            .apply {
                setOnQueryTextListener(
                    object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(query: String): Boolean {
                            adapter?.apply { filter.filter(query) }
                            return true
                        }

                        override fun onQueryTextSubmit(query: String?) = false
                    }
                )
                setOnCloseListener {
                    adapter?.apply { filter.filter("") }
                    false
                }
            }

        menu.findItem(R.id.save_app_list)
            .setOnMenuItemClickListener {
                adapter?.apply {
                    Globals.LOCKED_APPS = lockedApps
                    filter.filter("")
                }
                true
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}