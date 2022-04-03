package mufanc.tools.applock.fragment.apps

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import mufanc.tools.applock.R
import mufanc.tools.applock.databinding.FragmentAppsBinding

class AppsFragment : Fragment() {

    private var binding: FragmentAppsBinding? = null

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
                    adapter = AppListAdapter(model.appList)
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
                            (binding!!.applist.adapter as AppListAdapter).filter.filter(query)
                            return true
                        }

                        override fun onQueryTextSubmit(query: String?) = false
                    }
                )
                setOnCloseListener {
                    (binding!!.applist.adapter as AppListAdapter).filter.filter("")
                    false
                }
            }

        menu.findItem(R.id.save_app_list)
            .setOnMenuItemClickListener {
                Toast.makeText(requireContext(), "Not Implemented!", Toast.LENGTH_SHORT).show()
                true
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}