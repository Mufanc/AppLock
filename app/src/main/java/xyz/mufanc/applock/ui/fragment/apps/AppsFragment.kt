package xyz.mufanc.applock.ui.fragment.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.mufanc.applock.R
import xyz.mufanc.applock.core.util.Log
import xyz.mufanc.applock.databinding.FragmentAppsBinding
import xyz.mufanc.applock.ui.base.BaseFragment

class AppsFragment : BaseFragment<FragmentAppsBinding, AppsViewModel>() {

    companion object {
        private const val TAG = "AppsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        cache: Bundle?
    ): View? {
        requireActivity().addMenuProvider(AppsFragmentMenuProvider(), viewLifecycleOwner, Lifecycle.State.RESUMED)

        Log.i(TAG, "onCreateView")

        return super.onCreateView(inflater, container, cache).also {
            binding.run {
                appList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                appList.adapter = AppListAdapter(viewLifecycleOwner, model)
            }
        }
    }

    private class AppsFragmentMenuProvider : MenuProvider {
        override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.menu_fragment_apps, menu)
        }

        override fun onMenuItemSelected(item: MenuItem): Boolean {
            return true
        }

    }
}
