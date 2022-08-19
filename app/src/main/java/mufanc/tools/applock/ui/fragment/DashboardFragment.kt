package mufanc.tools.applock.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.R
import mufanc.tools.applock.core.xposed.AppLockManager
import mufanc.tools.applock.databinding.FragmentDashboardBinding
import mufanc.tools.applock.ui.fragment.base.BaseFragment
import mufanc.tools.applock.ui.viewmodel.DashboardViewModel
import mufanc.tools.applock.util.Globals

class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        val model = ViewModelProvider(this)[DashboardViewModel::class.java]
        with (binding) {
            status = model
            lifecycleOwner = this@DashboardFragment
            when (Globals.WORK_MODE) {
                "xposed" -> {
                    shizukuStatus.visibility = View.GONE
                }
                "shizuku" -> {
                    moduleActivated.visibility = View.GONE
                    hookerStatus.visibility = View.GONE
                }
            }

            if (model.requireReboot && !BuildConfig.DEBUG) {
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
}