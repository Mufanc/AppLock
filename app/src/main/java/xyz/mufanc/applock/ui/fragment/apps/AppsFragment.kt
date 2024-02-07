package xyz.mufanc.applock.ui.fragment.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.appcompat.widget.SearchView
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

        Log.d(TAG, "onCreateView")

        return super.onCreateView(inflater, container, cache).also {
            model.loading.value = true

            binding.run {
                appList.adapter = AppListAdapter(model, viewLifecycleOwner)
                appList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_apps_item_enter)

                appList.layoutAnimation = LayoutAnimationController(animation).apply {
                    order = LayoutAnimationController.ORDER_NORMAL
                    delay = 0.1f
                }

                props = model
                lifecycleOwner = this@AppsFragment
            }
        }
    }

    private inner class AppsFragmentMenuProvider : MenuProvider {
        override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.menu_fragment_apps, menu)

            (menu.findItem(R.id.search).actionView as? SearchView)!!.run {
                setOnQueryTextListener(
                    object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(query: String?): Boolean {
                            model.query.value = query
                            return true
                        }

                        override fun onQueryTextSubmit(query: String?) = false
                    }
                )

                setOnCloseListener {
                    model.query.value = ""
                    false
                }
            }
        }

        override fun onMenuItemSelected(item: MenuItem): Boolean {
            return true
        }
    }
}
