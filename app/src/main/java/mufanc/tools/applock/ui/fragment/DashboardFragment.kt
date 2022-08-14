package mufanc.tools.applock.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mufanc.tools.applock.BuildConfig
import mufanc.tools.applock.R
import mufanc.tools.applock.core.xposed.AppLockManager
import mufanc.tools.applock.databinding.FragmentDashboardBinding
import mufanc.tools.applock.ui.viewmodel.DashBoardViewModel
import mufanc.tools.applock.util.Globals

class DashboardFragment : Fragment() {

    private var binding: FragmentDashboardBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val model = ViewModelProvider(this)[DashBoardViewModel::class.java]
        binding?.apply {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}