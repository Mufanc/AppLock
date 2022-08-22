package mufanc.tools.applock.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.R
import mufanc.tools.applock.core.xposed.AppLockManager
import mufanc.tools.applock.databinding.FragmentDashboardBinding
import mufanc.tools.applock.util.Globals
import mufanc.tools.applock.util.Settings

class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        updateCardsVisibility()

        with (binding) {
            globals = Globals
            lifecycleOwner = this@DashboardFragment

            if (Globals.isServiceVersionOutdated && BuildConfig.DEBUG.not()) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.require_reboot)
                    .setMessage(R.string.core_version_not_match)
                    .setPositiveButton(R.string.reboot_now) { _, _ ->
                        AppLockManager.client?.reboot() ?: requireActivity().finish()
                    }
                    .setNegativeButton(R.string.reboot_later) { dialog, _ ->
                        dialog.dismiss()
                        requireActivity().finish()
                    }
                    .setCancelable(false)
                    .show()
            }

            return root
        }
    }

    override fun onResume() {
        super.onResume()
        updateCardsVisibility()
    }

    private fun updateCardsVisibility() {
        with (binding) {
            when (Settings.WORK_MODE.value) {
                Settings.WorkMode.XPOSED -> {
                    shizukuStatus.visibility = View.GONE
                    moduleActivated.visibility = View.VISIBLE
                    hookerStatus.visibility = View.VISIBLE
                }
                Settings.WorkMode.SHIZUKU -> {
                    shizukuStatus.visibility = View.VISIBLE
                    moduleActivated.visibility = View.GONE
                    hookerStatus.visibility = View.GONE
                }
            }
        }
    }
}